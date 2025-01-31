package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.framework.util.Bytes;
import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.io.TitanFileChannelChanface;
import com.pinecone.hydra.storage.TitanStorageReceiveIORequest;
import com.pinecone.hydra.storage.file.FrameSegmentNaming;
import com.pinecone.hydra.storage.file.KOFSFrameSegmentNaming;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.Verification;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.entity.RemoteFrame;
import com.pinecone.hydra.storage.file.transmit.UniformSourceLocator;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.volume.UnifiedTransmitConstructor;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.zip.CRC32;

public class TitanFileReceive64 implements FileReceive64{
    protected KOMFileSystem                 mKOMFileSystem;

    protected FrameSegmentNaming            mFrameSegmentNaming;

    protected UnifiedTransmitConstructor    constructor;

    protected Chanface                      chanface;

    protected FileNode                      fileNode;

    protected String                        destDirPath;

    protected VolumeManager                 volumeManager;


    public TitanFileReceive64( FileReceiveEntity64 entity ){
        this.mKOMFileSystem      = entity.getFileSystem();
        this.mFrameSegmentNaming = new KOFSFrameSegmentNaming();
        this.constructor         = new UnifiedTransmitConstructor();
        this.chanface            = entity.getChannel();
        this.destDirPath         = entity.getDestDirPath();
        this.fileNode            = entity.getFile();
        this.volumeManager       = entity.getVolumeManager();
    }

    @Override
    public void receive( LogicVolume volume ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        long frameSize = this.mKOMFileSystem.getConfig().getFrameSize().longValue();
        this.fileNode.setGuid( mKOMFileSystem.queryGUIDByPath( this.destDirPath ) );

        FSNodeAllotment allotment = mKOMFileSystem.getFSNodeAllotment();
        long segId = 0;
        long currentPosition = 0;
        long endSize = frameSize;

        StorageIOResponse storageIOResponse = null;
        while (true) {
            if( currentPosition >= fileNode.getDefinitionSize() ){
                break;
            }

            if( currentPosition + endSize > fileNode.getDefinitionSize() ){
                endSize = fileNode.getDefinitionSize() - currentPosition;
            }

            LocalFrame localFrame = allotment.newLocalFrame();
            RemoteFrame remoteFrame = allotment.newRemoteFrame( fileNode.getGuid(),(int)segId );
            remoteFrame.setDeviceGuid(this.mKOMFileSystem.getConfig().getLocalhostGUID());
            remoteFrame.setSegGuid( localFrame.getSegGuid() );

            StorageReceiveIORequest storageReceiveIORequest = new TitanStorageReceiveIORequest();
            storageReceiveIORequest.setSize( fileNode.getDefinitionSize() );
            storageReceiveIORequest.setName( fileNode.getName() );
            storageReceiveIORequest.setStorageObjectGuid( localFrame.getSegGuid() );

            //storageIOResponse = volume.channelReceive(storageReceiveIORequest, kChannel, currentPosition, endSize);
            ReceiveEntity receiveEntity = this.constructor.getReceiveEntity(volume.getClass(), this.volumeManager, storageReceiveIORequest, this.chanface, volume);
            storageIOResponse = volume.receive( receiveEntity, currentPosition, endSize );

            UniformSourceLocator uniformSourceLocator = new UniformSourceLocator();
            if( storageIOResponse != null ){
                localFrame.setCrc32(String.valueOf(storageIOResponse.getCre32().getValue()));
            }
            uniformSourceLocator.setVolumeGuid( volume.getGuid().toString() );
            localFrame.setSize( endSize );
            localFrame.setSourceName( uniformSourceLocator.toJSONString() );
            localFrame.setFileGuid( fileNode.getGuid() );
            localFrame.setSegId( segId );

            segId++;
            localFrame.save();
            remoteFrame.save();
            currentPosition += endSize;
        }



        fileNode.setPhysicalSize( currentPosition );
        fileNode.setLogicSize( currentPosition );
        mKOMFileSystem.update( fileNode );

        Verification verification = this.getVerification();
        fileNode.setChecksum( verification.getChecksum() );
        fileNode.setParityCheck( verification.getParityCheck() );
        fileNode.setCrc32Xor( Long.toHexString(verification.getCrc32().getValue()) );
        mKOMFileSystem.update( fileNode );
    }

    @Override
    public void receive(LogicVolume volume, Number offset, Number endSize) throws IOException {

    }

    Verification getVerification() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        File tempFile = File.createTempFile("temp",".temp");
        FileNode fileNode = (FileNode)this.mKOMFileSystem.get(this.mKOMFileSystem.queryGUIDByPath(this.destDirPath));
        FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelChanface kChannel = new TitanFileChannelChanface(channel);
        TitanFileExportEntity64 exportEntity = new TitanFileExportEntity64(this.mKOMFileSystem, this.volumeManager, fileNode, kChannel);
        this.mKOMFileSystem.export( exportEntity );

        return getVerification(tempFile);
    }

    private Verification getVerification(File tempFile) throws IOException {
        Verification verification = new Verification();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(tempFile))) {
            CRC32 crc = new CRC32();
            long checksum = 0;
            int parityCheck = 0;

            // 使用一个缓冲区一次读取多个字节
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    byte b = buffer[i];

                    // 批量处理每个字节
                    parityCheck += Bytes.calculateParity(b);
                    checksum += b & 0xFF;
                    crc.update(b);
                }
            }

            verification.setChecksum(checksum);
            verification.setCrc32(crc);
            verification.setParityCheck(parityCheck);
        }
        return verification;
    }
}
