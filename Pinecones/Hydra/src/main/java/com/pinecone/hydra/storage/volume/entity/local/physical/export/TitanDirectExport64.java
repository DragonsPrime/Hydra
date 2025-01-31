package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlockStatus;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class TitanDirectExport64 implements DirectExport64{

    protected VolumeManager volumeManager;

    protected StorageExportIORequest storageExportIORequest;

    public TitanDirectExport64( DirectExportEntity64 entity ){
        this.volumeManager = entity.getVolumeManager();
        this.storageExportIORequest = entity.getStorageIORequest();
    }

    @Override
    public StorageIOResponse export( Chanface chanface ) throws IOException {
        String sourceName = this.storageExportIORequest.getSourceName();
        long size = this.storageExportIORequest.getSize().longValue();
        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();

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
            chanface.write(buffer);
            buffer.clear();

            titanMiddleStorageObject.setChecksum( checksum );
            titanMiddleStorageObject.setCrc32( crc );
            titanMiddleStorageObject.setParityCheck( parityCheck );
        }

        return titanMiddleStorageObject;
    }

    @Override
    public StorageIOResponse export(RandomAccessChanface randomAccessChanface) throws IOException {
        return null;
    }

    @Override
    public StorageIOResponse export( CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) {
        String sourceName = this.storageExportIORequest.getSourceName();
        TitanStorageIOResponse titanMiddleStorageObject = new TitanStorageIOResponse();

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
            Debug.trace( "起始位置" + offset.longValue()+"终止大小"+bufferSize+"缓存大小"+endSize.intValue() );
            byteBuffer.get(buffer, cacheBlock.getByteStart().intValue(), (int) bufferSize);
            cacheBlock.setStatus( CacheBlockStatus.Full );
            cacheBlock.setValidByteStart( cacheBlock.getByteStart().intValue() );
            cacheBlock.setValidByteEnd( cacheBlock.getByteStart().intValue()+bufferSize );

            // 计算校验和和奇偶校验
            CRC32 crc = new CRC32();
            for (int i = 0; i < endSize.intValue(); i++) {
                byte b = buffer[cacheBlock.getByteStart().intValue()+i];
                parityCheck += Bytes.calculateParity(b);
                checksum += b & 0xFF;
                crc.update(b);
            }

            titanMiddleStorageObject.setChecksum(checksum);
            titanMiddleStorageObject.setCrc32(crc);
            titanMiddleStorageObject.setParityCheck(parityCheck);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return titanMiddleStorageObject;
    }
}
