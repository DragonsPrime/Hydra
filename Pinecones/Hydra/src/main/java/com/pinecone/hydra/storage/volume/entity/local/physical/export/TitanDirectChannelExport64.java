package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.TitanMiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

public class TitanDirectChannelExport64 implements DirectChannelExport64{
    @Override
    public MiddleStorageObject export(DirectChannelExportEntity entity) throws IOException {
        ExportStorageObject exportStorageObject = entity.getExportStorageObject();
        String sourceName = exportStorageObject.getSourceName();
        long size = exportStorageObject.getSize().longValue();
        FileChannel channel = entity.getChannel();
        TitanMiddleStorageObject titanMiddleStorageObject = new TitanMiddleStorageObject();

        long parityCheck = 0;
        long checksum = 0;
        File file = new File(sourceName);

        try (FileChannel frameChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) size);
            frameChannel.read(buffer);
            buffer.flip();
            CRC32 crc = new CRC32();
            while ( buffer.hasRemaining() ) {
                byte b = buffer.get();
                parityCheck += Bytes.calculateParity( b );
                checksum += b & 0xFF;
                crc.update(b);
            }

            buffer.rewind();
            channel.write(buffer);
            buffer.clear();

            titanMiddleStorageObject.setChecksum( checksum );
            titanMiddleStorageObject.setCrc32( Long.toHexString(crc.getValue()) );
            titanMiddleStorageObject.setParityCheck( parityCheck );
        }

        return titanMiddleStorageObject;
    }

    @Override
    public MiddleStorageObject raid0Export(DirectChannelExportEntity entity, byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter) {
        VolumeManager volumeManager = entity.getVolumeManager();
        FileChannel targetChannel = entity.getChannel();
        Number stripSize = volumeManager.getConfig().getDefaultStripSize();
        long bufferStartPosition = stripSize.longValue() * jobCode; // 传入的 buffer 起始位置
        ExportStorageObject exportStorageObject = entity.getExportStorageObject();
        String sourceName = exportStorageObject.getSourceName();
        TitanMiddleStorageObject titanMiddleStorageObject = new TitanMiddleStorageObject();

        long parityCheck = 0;
        long checksum = 0;
        File file = new File(sourceName);

        try (FileChannel frameChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            long bufferSize = endSize.longValue();
            // 定位到文件的 offset 位置
            frameChannel.position(offset.longValue());

            // 读取 endSize 大小的字节
            ByteBuffer byteBuffer = ByteBuffer.allocate(endSize.intValue());
            int read = frameChannel.read(byteBuffer);
            byteBuffer.flip();

            // 将读取的数据从 bufferStartPosition 开始写入到 buffer
            if( read < bufferSize ){
                bufferSize = read;
            }
            //Debug.trace( "起始位置" + bufferStartPosition+"终止大小"+bufferSize );
            byteBuffer.get(buffer, (int) bufferStartPosition, (int) bufferSize);

            counter.incrementAndGet();
            if( counter.get() == jobNum ){
                ByteBuffer writeBuffer = ByteBuffer.wrap(buffer);
                targetChannel.write(writeBuffer);
                // 清空 buffer
                Arrays.fill(buffer, (byte) 0);
                counter.set( 0 );
            }

            // 计算校验和和奇偶校验
            CRC32 crc = new CRC32();
            for (int i = 0; i < endSize.intValue(); i++) {
                byte b = buffer[(int) bufferStartPosition + i];
                parityCheck += Bytes.calculateParity(b);
                checksum += b & 0xFF;
                crc.update(b);
            }

            titanMiddleStorageObject.setChecksum(checksum);
            titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
            titanMiddleStorageObject.setParityCheck(parityCheck);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return titanMiddleStorageObject;
    }

}
