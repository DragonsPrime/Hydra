package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.framework.util.Bytes;
import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageNaming;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.TitanStorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageNaming;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.zip.CRC32;

public class TitanDirectReceive64 implements DirectReceive64{
    protected StorageNaming           storageNaming;

    protected StorageReceiveIORequest storageReceiveIORequest;

    protected VolumeManager           volumeManager;

    protected String                  destDirPath;

    public TitanDirectReceive64( DirectReceiveEntity entity ){
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.volumeManager           = entity.getVolumeManager();
        this.destDirPath             = entity.getDestDirPath();
        this.storageNaming           = new TitanStorageNaming();
    }

    @Override
    public StorageIOResponse receive(Chanface chanface) throws IOException {
        return this.receiveWithOffsetAndSize( chanface, 0, this.storageReceiveIORequest.getSize().intValue() );
    }

    @Override
    public StorageIOResponse receive( Chanface chanface, Number offset, Number endSize) throws IOException {
        return this.receiveWithOffsetAndSize( chanface,offset.intValue(),endSize.intValue() );
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.receiveWithOffsetAndSize( randomAccessChanface, 0, this.storageReceiveIORequest.getSize().intValue() );
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface, Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.receiveWithOffsetAndSize( randomAccessChanface,offset.intValue(),endSize.intValue() );
    }

    @Override
    public StorageIOResponse receive(Chanface chanface,CacheBlock cacheBlock, byte[] buffer) throws IOException {
        int start = cacheBlock.getValidByteStart().intValue();
        int end = cacheBlock.getValidByteEnd().intValue();

        if (start < 0 || end > buffer.length || start >= end) {
            throw new IllegalArgumentException("Invalid cacheBlock range or buffer size.");
        }

        int size = end - start;
        int parityCheck = 0;
        long checksum = 0;
        CRC32 crc = new CRC32();

        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();
        titanMiddleStorageObject.setObjectGuid(storageReceiveIORequest.getStorageObjectGuid());

        String sourceName = this.storageNaming.naming(storageReceiveIORequest.getName(), storageReceiveIORequest.getStorageObjectGuid().toString());
        Path path = Paths.get(destDirPath, sourceName);

        Files.createDirectories(path.getParent());

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (int i = start; i < end; i++) {
                byte b = buffer[i];
                parityCheck += Bytes.calculateParity(b);
                checksum += b & 0xFF;
                crc.update(b);
            }

            outputStream.write(buffer, start, size);
        } catch (IOException e) {
            throw new IOException("Failed to write to file: " + path.toString(), e);
        }

        titanMiddleStorageObject.setChecksum(checksum);
        titanMiddleStorageObject.setCrc32(crc);
        titanMiddleStorageObject.setParityCheck(parityCheck);
        titanMiddleStorageObject.setSourceName(path.toString());

        return titanMiddleStorageObject;
    }

    private StorageIOResponse receiveWithOffsetAndSize(Chanface chanface,long offset, int size) throws IOException {
        //Debug.trace("缓存的是"+offset+"到"+(offset + size));

        int parityCheck = 0;
        long checksum = 0;
        //ByteBuffer buffer = ByteBuffer.allocateDirect(size);

        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();
        titanMiddleStorageObject.setObjectGuid(storageReceiveIORequest.getStorageObjectGuid());

        //buffer.clear();

        ByteBuffer[] lpBuf = new ByteBuffer[ 1 ];
        chanface.read( (out)->{
            lpBuf[0] = out;
        }, size, offset );
        ByteBuffer buffer = lpBuf[ 0 ];

        buffer.flip();
        CRC32 crc = new CRC32();

        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            parityCheck += Bytes.calculateParity(b);
            checksum += b & 0xFF;
            crc.update(b);
        }
        URI uri = null;
        try {
            uri = new URI( this.destDirPath );
        }
        catch ( URISyntaxException e ) {
            throw new IOException(e);
        }

        Path path = Paths.get(uri);
        String sourceName = this.storageNaming.naming(
                this.storageReceiveIORequest.getName(), this.storageReceiveIORequest.getStorageObjectGuid().toString()
        );
        path = path.resolve(sourceName);

        try (FileChannel chunkChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            buffer.rewind();
            chunkChannel.position(chunkChannel.size());  // 从文件末尾开始写入
            chunkChannel.write(buffer);
        }

        titanMiddleStorageObject.setChecksum(checksum);
        titanMiddleStorageObject.setCrc32(crc);
        titanMiddleStorageObject.setParityCheck(parityCheck);
        titanMiddleStorageObject.setSourceName(path.toString());

        return titanMiddleStorageObject;
    }


}
