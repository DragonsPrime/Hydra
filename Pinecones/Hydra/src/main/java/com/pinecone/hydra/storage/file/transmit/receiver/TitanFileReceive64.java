package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.TitanStorageReceiveIORequest;
import com.pinecone.hydra.storage.file.FrameSegmentNaming;
import com.pinecone.hydra.storage.file.KOFSFrameSegmentNaming;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.entity.RemoteFrame;
import com.pinecone.hydra.storage.file.transmit.UniformSourceLocator;
import com.pinecone.hydra.storage.volume.UnifiedTransmitConstructor;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanFileReceive64 implements FileReceive64{
    protected KOMFileSystem                 mKOMFileSystem;

    protected FrameSegmentNaming            mFrameSegmentNaming;

    protected UnifiedTransmitConstructor    constructor;

    protected Chanface chanface;

    protected FileNode                      fileNode;

    protected String                        destDirPath;

    protected VolumeManager                 volumeManager;


    public TitanFileReceive64( FileReceiveEntity64 entity ){
        this.mKOMFileSystem      = entity.getFileSystem();
        this.mFrameSegmentNaming = new KOFSFrameSegmentNaming();
        this.constructor         = new UnifiedTransmitConstructor();
        this.chanface = entity.getChannel();
        this.destDirPath         = entity.getDestDirPath();
        this.fileNode            = entity.getFile();
        this.volumeManager       = entity.getVolumeManager();
    }

    @Override
    public void receive( LogicVolume volume ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        long frameSize = this.mKOMFileSystem.getConfig().getFrameSize().longValue();
        this.fileNode.setGuid( mKOMFileSystem.queryGUIDByPath( this.destDirPath ) );

        int parityCheck = 0;
        long checksum = 0;
        long crc32Xor = 0;

        FSNodeAllotment allotment = mKOMFileSystem.getFSNodeAllotment();
        long segId = 0;
        long currentPosition = 0;
        long endSize = frameSize;
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
            StorageIOResponse storageIOResponse = null;
            //storageIOResponse = volume.channelReceive(storageReceiveIORequest, kChannel, currentPosition, endSize);
            ReceiveEntity receiveEntity = this.constructor.getReceiveEntity(volume.getClass(), this.volumeManager, storageReceiveIORequest, this.chanface, volume);
            storageIOResponse = volume.receive( receiveEntity, currentPosition, endSize );

            UniformSourceLocator uniformSourceLocator = new UniformSourceLocator();
            if( storageIOResponse != null ){
                localFrame.setCrc32( storageIOResponse.getCre32() );
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
        fileNode.setChecksum( checksum );
        fileNode.setParityCheck( parityCheck );
        fileNode.setCrc32Xor( Long.toHexString(crc32Xor) );
        mKOMFileSystem.update( fileNode );
    }

    @Override
    public void receive(LogicVolume volume, Number offset, Number endSize) throws IOException {

    }
}
