package com.pinecone.hydra.storage.volume.entity.local.physical.receive.stream;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageNaming;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.TitanStorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageNaming;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class TitanDirectStreamReceive64 implements DirectStreamReceive64{
    private StorageNaming storageNaming;
    public  TitanDirectStreamReceive64(){
        this.storageNaming = new TitanStorageNaming();
    }

    @Override
    public StorageIOResponse receive(DirectStreamReceiveEntity entity) throws IOException {
        return receiveWithOffsetAndSize(entity, 0, entity.getReceiveStorageObject().getSize().intValue());
    }

    @Override
    public StorageIOResponse receive(DirectStreamReceiveEntity entity, Number offset, Number endSize) throws IOException {
        return receiveWithOffsetAndSize(entity, 0, entity.getReceiveStorageObject().getSize().intValue());
    }

    @Override
    public StorageIOResponse receive(DirectStreamReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer) throws IOException {
        StorageReceiveIORequest storageReceiveIORequest = entity.getReceiveStorageObject();
        String destDirPath = entity.getDestDirPath();
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

    private StorageIOResponse receiveWithOffsetAndSize(DirectStreamReceiveEntity entity, long offset, int size) throws IOException {
        StorageReceiveIORequest storageReceiveIORequest = entity.getReceiveStorageObject();
        String destDirPath = entity.getDestDirPath();
        InputStream inputStream = entity.getStream();

        int parityCheck = 0;
        long checksum = 0;
        CRC32 crc = new CRC32();

        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();
        titanMiddleStorageObject.setObjectGuid(storageReceiveIORequest.getStorageObjectGuid());

        String sourceName = this.storageNaming.naming(storageReceiveIORequest.getName(), storageReceiveIORequest.getStorageObjectGuid().toString());
        Path path = Paths.get(destDirPath, sourceName);

        // 创建文件及其父目录（如果尚未存在）
        Files.createDirectories(path.getParent());

        long skipped = inputStream.skip(offset);
        if (skipped < offset) {
            throw new IOException("Failed to skip to the specified offset: " + offset);
        }

        byte[] buffer = new byte[8192];
        int bytesRead;
        int remaining = size;

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            while (remaining > 0 && (bytesRead = inputStream.read(buffer, 0, Math.min(buffer.length, remaining))) != -1) {
                remaining -= bytesRead;

                for (int i = 0; i < bytesRead; i++) {
                    byte b = buffer[i];
                    parityCheck += Bytes.calculateParity(b);
                    checksum += b & 0xFF;
                    crc.update(b);
                }

                outputStream.write(buffer, 0, bytesRead);
            }
        }

        titanMiddleStorageObject.setChecksum(checksum);
        titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
        titanMiddleStorageObject.setParityCheck(parityCheck);
        titanMiddleStorageObject.setSourceName(path.toString());

        return titanMiddleStorageObject;
    }
}
