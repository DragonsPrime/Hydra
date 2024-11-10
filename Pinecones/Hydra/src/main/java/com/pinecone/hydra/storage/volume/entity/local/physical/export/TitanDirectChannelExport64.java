package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.TitanMiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripChannelBufferToFileJob;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.ulf.util.id.GUID72;
import com.pinecone.ulf.util.id.GUIDs;

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
    public MiddleStorageObject raid0Export(DirectChannelExportEntity entity, byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter, StripLockEntity lockEntity) {
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
                this.bufferToFile( buffer, targetChannel, volumeManager );
                counter.getAndSet( 0 );
                lockEntity.getCurrentBufferCode().incrementAndGet();
                if ( lockEntity.getCurrentBufferCode().get() == jobNum ){
                    lockEntity.getCurrentBufferCode().getAndSet( 0 );
                }
                for( Object lockObject : lockEntity.getLockGroup() ){
                    synchronized ( lockObject ){
                        lockObject.notify();
                    }
                }
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

    private void bufferToFile( byte[] buffer, FileChannel channel, VolumeManager volumeManager ){
        Hydrarum hydrarum = volumeManager.getHydrarum();
        GUID72 guid72 = GUIDs.Dummy72();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( guid72.toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        TitanStripChannelBufferToFileJob job = new TitanStripChannelBufferToFileJob(buffer, channel);
        LocalStripedTaskThread taskThread = new LocalStripedTaskThread( guid72.toString(),masterVolumeGram,job);
        masterVolumeGram.getTaskManager().add( taskThread );
        taskThread.start();
    }

}
