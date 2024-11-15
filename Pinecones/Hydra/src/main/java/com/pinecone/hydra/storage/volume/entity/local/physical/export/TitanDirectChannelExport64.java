package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.TitanMiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlockStatus;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripTerminalStateRecord;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

public class TitanDirectChannelExport64 implements DirectChannelExport64{
    @Override
    public MiddleStorageObject export( DirectChannelExportEntity entity ) throws IOException {
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

/*    public byte[] export( DirectChannelExportEntity outputEntity, Number offset, Number readSome, long bufferStartPosition ) {
        byte[] bytes;
        this.export(outputEntity, offset, endSize, bufferStartPosition, new ChannelBytesExportRecall() {
            @Override
            public void exportTo(Object toMe) {
                ByteBuffer byteBuffer = (ByteBuffer) toMe;
                bytes = new byte[]
                byteBuffer.get(bytes, (int) offset.longValue(), (int) readSome.intValue());

            }
        });

        return bytes;
    }*/

    public MiddleStorageObject export( DirectChannelExportEntity outputEntity, Number offset, Number endSize, long bufferStartPosition, ChannelBytesExportRecall recall ) {
        VolumeManager volumeManager = outputEntity.getVolumeManager();
        FileChannel targetChannel = outputEntity.getChannel();
        Number stripSize = volumeManager.getConfig().getDefaultStripSize();

        ExportStorageObject exportStorageObject = outputEntity.getExportStorageObject();
        String sourceName = exportStorageObject.getSourceName();
        TitanMiddleStorageObject titanMiddleStorageObject = new TitanMiddleStorageObject();

        long parityCheck = 0;
        long checksum = 0;
        File file = new File(sourceName);

        try ( FileChannel frameChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ) ) {
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
            //byteBuffer.get(outputTarget, (int) bufferStartPosition, (int) bufferSize);

            recall.exportTo( byteBuffer );

            //....
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return titanMiddleStorageObject;
    }


    @Override
    public MiddleStorageObject raid0Export(DirectChannelExportEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity ) {
        VolumeManager volumeManager = entity.getVolumeManager();
        ExportStorageObject exportStorageObject = entity.getExportStorageObject();
        String sourceName = exportStorageObject.getSourceName();
        TitanMiddleStorageObject titanMiddleStorageObject = new TitanMiddleStorageObject();
        byte[] outputTarget = flyweightEntity.getBuffer();

        long parityCheck = 0;
        long checksum = 0;
        File file = new File(sourceName);

        try ( FileChannel frameChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ) ) {
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
            //Debug.trace( "起始位置" + offset.longValue()+"终止大小"+bufferSize+"缓存大小"+endSize.intValue() );
            byteBuffer.get(outputTarget, cacheBlock.getByteStart().intValue(), (int) bufferSize);
            cacheBlock.setStatus( CacheBlockStatus.Full );
            cacheBlock.setValidByteStart( cacheBlock.getByteStart().intValue() );
            cacheBlock.setValidByteEnd( cacheBlock.getByteStart().intValue()+bufferSize );

            // 计算校验和和奇偶校验
            CRC32 crc = new CRC32();
            for (int i = 0; i < endSize.intValue(); i++) {
                byte b = outputTarget[cacheBlock.getByteStart().intValue()+i];
                parityCheck += Bytes.calculateParity(b);
                checksum += b & 0xFF;
                crc.update(b);
            }

            titanMiddleStorageObject.setChecksum(checksum);
            titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
            titanMiddleStorageObject.setParityCheck(parityCheck);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return titanMiddleStorageObject;
    }

//    @Override
//    public MiddleStorageObject raid0Export(DirectChannelExportEntity entity, byte[] outputTarget, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity ) {
//        VolumeManager volumeManager = entity.getVolumeManager();
//        FileChannel targetChannel = entity.getChannel();
//        Number stripSize = volumeManager.getConfig().getDefaultStripSize();
//        long bufferStartPosition = stripSize.longValue() * flyweightEntity.getJobCode(); // 传入的 buffer 起始位置
//        ExportStorageObject exportStorageObject = entity.getExportStorageObject();
//        String sourceName = exportStorageObject.getSourceName();
//        TitanMiddleStorageObject titanMiddleStorageObject = new TitanMiddleStorageObject();
//        boolean isLast = false;
//
//        long parityCheck = 0;
//        long checksum = 0;
//        File file = new File(sourceName);
//
//        try ( FileChannel frameChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ) ) {
//            long bufferSize = endSize.longValue();
//            // 定位到文件的 offset 位置
//            frameChannel.position(offset.longValue());
//
//            // 读取 endSize 大小的字节
//            ByteBuffer byteBuffer = ByteBuffer.allocate(endSize.intValue());
//            int read = frameChannel.read(byteBuffer);
//            byteBuffer.flip();
//            if (read == -1) {
//                return null;
//            }
//
//            // 将读取的数据从 bufferStartPosition 开始写入到 buffer
//            if( read < bufferSize ){
//                bufferSize = read;
//            }
//            //Debug.trace( "起始位置" + bufferStartPosition+"终止大小"+bufferSize );
//            byteBuffer.get(outputTarget, (int) bufferStartPosition, (int) bufferSize);
//
//
//            // 监工形态
//            while (true) {
//                // 使用 compareAndSet 实现安全增量
//                int currentCount = flyweightEntity.getCounter().get();
//                if (currentCount < 2 && flyweightEntity.getCounter().compareAndSet(currentCount, currentCount + 1)) {
//                    if (flyweightEntity.getCounter().get() == 2) {
//
//                        flyweightEntity.getCounter().set(0);  // 重置 counter
//
//                        // 更新 buffer code 安全递增和重置
//                        if (flyweightEntity.getLockEntity().getCurrentBufferCode().incrementAndGet() == 2) {
//                            flyweightEntity.getLockEntity().getCurrentBufferCode().set(0);
//                        }
//
//                        flyweightEntity.getLockEntity().unlockPipeStage();
//                        Debug.warnSyn( "sss" );
//                        break;
//                    }
//                    Debug.warnSyn( "a1" );
//                }
//                else {
//                    try {
//                        ( (Semaphore) flyweightEntity.getLockEntity().getLockObject() ).acquire();
//                        break;
//                    }
//                    catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        e.printStackTrace();
//                    }
//
////                    synchronized (lockEntity.getLockObject()) {
////                        try {
////                            lockEntity.getLockObject().wait();
////                            break;
////                        }
////                        catch (InterruptedException e) {
////                            Thread.currentThread().interrupt();  // 重设线程的中断状态
////                            e.printStackTrace();
////                        }
////                    }
//                }
//            }
//
//
////                if( counter.incrementAndGet() == 2 ){
////                        if( isLast ){
////                            int temporaryCurrentPosition = 0;
////                            terminalStateRecordGroup.sort(Comparator.comparing( TerminalStateRecord :: getSequentialNumbering ));
////                            int totalSize = terminalStateRecordGroup.stream()
////                                    .mapToInt(stateRecord -> stateRecord.getValidByteEnd().intValue() - stateRecord.getValidByteStart().intValue())
////                                    .sum();
////                            byte[] temporaryBuffer = new byte[ totalSize ];
////                            for( TerminalStateRecord stateRecord : terminalStateRecordGroup ){
////                                int length = stateRecord.getValidByteEnd().intValue() - stateRecord.getValidByteStart().intValue();
////                                ByteBuffer buffer = ByteBuffer.wrap(outputTarget, stateRecord.getValidByteStart().intValue(), length);
////                                buffer.get( temporaryBuffer,temporaryCurrentPosition, length  );
////                                temporaryCurrentPosition += length;
////                            }
////                            outputTarget = temporaryBuffer;
////                        }
////                        //this.bufferToFile( outputTarget, targetChannel, volumeManager );
////                        counter.getAndSet( 0 );
////                        if ( lockEntity.getCurrentBufferCode().incrementAndGet() == 2 ){
////                            lockEntity.getCurrentBufferCode().getAndSet( 0 );
////                        }
////
////                        lockEntity.unlockPipeStage();
////                }
////                else {
////                    try {
////                        ( (Semaphore) lockEntity.getLockObject() ).acquire();
////                    }
////                    catch (InterruptedException e) {
////                        Thread.currentThread().interrupt();
////                        e.printStackTrace();
////                    }
//////                    synchronized (lockEntity.getLockObject()) {
//////                        try {
//////                            lockEntity.getLockObject().wait();
//////                        }
//////                        catch (InterruptedException e) {
//////                            Thread.currentThread().interrupt();
//////                            e.printStackTrace();
//////                        }
//////                    }
////                }
//
//
//            // 计算校验和和奇偶校验
//            CRC32 crc = new CRC32();
//            for (int i = 0; i < endSize.intValue(); i++) {
//                byte b = outputTarget[(int) bufferStartPosition + i];
//                parityCheck += Bytes.calculateParity(b);
//                checksum += b & 0xFF;
//                crc.update(b);
//            }
//
//            titanMiddleStorageObject.setChecksum(checksum);
//            titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
//            titanMiddleStorageObject.setParityCheck(parityCheck);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//
//
//        return titanMiddleStorageObject;
//    }


}
