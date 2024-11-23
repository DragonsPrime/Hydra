package com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageNaming;
import com.pinecone.hydra.storage.TemporaryLock;
import com.pinecone.hydra.storage.TitanStorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageNaming;
import com.pinecone.hydra.storage.StorageReceiveIORequest;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class TitanDirectChannelReceive64 implements DirectChannelReceive64{
    private StorageNaming       storageNaming;
    public  TitanDirectChannelReceive64(){
        this.storageNaming = new TitanStorageNaming();
    }
    @Override
    public StorageIOResponse receive(DirectChannelReceiveEntity entity) throws IOException {
        return receiveWithOffsetAndSize(entity, 0, entity.getReceiveStorageObject().getSize().intValue());
    }

    @Override
    public StorageIOResponse receive(DirectChannelReceiveEntity entity, Number offset, Number endSize) throws IOException {
        return receiveWithOffsetAndSize(entity, offset.longValue(), endSize.intValue());
    }

    private  StorageIOResponse receiveWithOffsetAndSize(DirectChannelReceiveEntity entity, long offset, int size) throws IOException {
        TemporaryLock.reentrantLock.lock();
        Debug.trace("缓存的是"+offset+"到"+(offset + size));
        StorageReceiveIORequest storageReceiveIORequest = entity.getReceiveStorageObject();
        String destDirPath = entity.getDestDirPath();
        KChannel channel = entity.getChannel();

        int parityCheck = 0;
        long checksum = 0;
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);

        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();
        titanMiddleStorageObject.setObjectGuid(storageReceiveIORequest.getStorageObjectGuid());

        buffer.clear();
//        channel.position(offset);
//        channel.read(buffer);
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

        TemporaryLock.reentrantLock.unlock();
        return titanMiddleStorageObject;
    }

}
