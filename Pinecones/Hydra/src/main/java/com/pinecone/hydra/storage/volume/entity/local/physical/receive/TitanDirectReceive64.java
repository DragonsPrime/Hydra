package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.framework.util.Bytes;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageNaming;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.TitanStorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageNaming;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class TitanDirectReceive64 implements DirectReceive64{
    protected StorageNaming           storageNaming;

    protected KChannel                channel;

    protected StorageReceiveIORequest storageReceiveIORequest;

    protected VolumeManager           volumeManager;

    protected String                  destDirPath;

    public TitanDirectReceive64( DirectReceiveEntity entity ){
        this.channel                 = entity.getKChannel();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.volumeManager           = entity.getVolumeManager();
        this.destDirPath             = entity.getDestDirPath();
        this.storageNaming           = new TitanStorageNaming();
    }

    @Override
    public StorageIOResponse receive() throws IOException {
        return this.receiveWithOffsetAndSize( 0, this.storageReceiveIORequest.getSize().intValue() );
    }

    @Override
    public StorageIOResponse receive( Number offset, Number endSize) throws IOException {
        return this.receiveWithOffsetAndSize( offset.intValue(),endSize.intValue() );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException {
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
        titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
        titanMiddleStorageObject.setParityCheck(parityCheck);
        titanMiddleStorageObject.setSourceName(path.toString());

        return titanMiddleStorageObject;
    }

    private  StorageIOResponse receiveWithOffsetAndSize(long offset, int size) throws IOException {
        //Debug.trace("缓存的是"+offset+"到"+(offset + size));

        int parityCheck = 0;
        long checksum = 0;
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);

        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();
        titanMiddleStorageObject.setObjectGuid(storageReceiveIORequest.getStorageObjectGuid());

        buffer.clear();
        channel.read( buffer, offset );
        buffer.flip();
        CRC32 crc = new CRC32();

        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            parityCheck += Bytes.calculateParity(b);
            checksum += b & 0xFF;
            crc.update(b);
        }
        String sourceName = this.storageNaming.naming(storageReceiveIORequest.getName(), storageReceiveIORequest.getStorageObjectGuid().toString());
        Path path = Paths.get(destDirPath, sourceName);

        try (FileChannel chunkChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            buffer.rewind();
            chunkChannel.position(chunkChannel.size());  // 从文件末尾开始写入
            chunkChannel.write(buffer);
        }

        titanMiddleStorageObject.setChecksum(checksum);
        titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
        titanMiddleStorageObject.setParityCheck(parityCheck);
        titanMiddleStorageObject.setSourceName(path.toString());

        return titanMiddleStorageObject;
    }
}
