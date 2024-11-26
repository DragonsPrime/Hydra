package com.pinecone.hydra.storage.volume.entity.local.physical.export.stream;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlockStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class TitanDirectStreamExport64 implements DirectStreamExport64 {
    @Override
    public StorageIOResponse export(DirectStreamExportEntity entity) throws IOException {
        StorageExportIORequest storageExportIORequest = entity.getStorageIORequest();
        String sourceName = storageExportIORequest.getSourceName();
        long size = storageExportIORequest.getSize().longValue();
        OutputStream outputStream = entity.getStream();
        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();

        long parityCheck = 0;
        long checksum = 0;
        File file = new File(sourceName);

        CRC32 crc = new CRC32();
        byte[] buffer = new byte[8192];
        int bytesRead;
        long remaining = size;

        try (InputStream inputStream = new FileInputStream(file)) {
            while (remaining > 0 && (bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                remaining -= bytesRead;


                for (int i = 0; i < bytesRead; i++) {
                    byte b = buffer[i];
                    parityCheck += Bytes.calculateParity(b);
                    checksum += b & 0xFF;
                    crc.update(b);
                }


                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
        }

        titanMiddleStorageObject.setChecksum(checksum);
        titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
        titanMiddleStorageObject.setParityCheck(parityCheck);

        return titanMiddleStorageObject;
    }

    @Override
    public StorageIOResponse export(DirectStreamExportEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) {
        StorageExportIORequest storageExportIORequest = entity.getStorageIORequest();
        String sourceName = storageExportIORequest.getSourceName();
        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();

        long parityCheck = 0;
        long checksum = 0;
        File file = new File(sourceName);

        try (InputStream inputStream = new FileInputStream(file)) {

            long skipped = inputStream.skip(offset.longValue());

            int read = inputStream.read(buffer, cacheBlock.getByteStart().intValue(), endSize.intValue());

            cacheBlock.setStatus(CacheBlockStatus.Full);
            cacheBlock.setValidByteStart(cacheBlock.getByteStart().intValue());
            cacheBlock.setValidByteEnd(cacheBlock.getByteStart().intValue() + read);

            CRC32 crc = new CRC32();
            for (int i = 0; i < read; i++) {
                byte b = buffer[cacheBlock.getByteStart().intValue() + i];
                parityCheck += Bytes.calculateParity(b);
                checksum += b & 0xFF;
                crc.update(b);
            }

            titanMiddleStorageObject.setChecksum(checksum);
            titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
            titanMiddleStorageObject.setParityCheck(parityCheck);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during file I/O operation.", e);
        }

        return titanMiddleStorageObject;
    }

}
