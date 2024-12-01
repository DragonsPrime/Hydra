package com.pinecone.hydra.storage;

import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlockStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TitanFileChannelChanface implements Chanface {
    private final FileChannel       channel;
    private final ReentrantLock     reentrantLock;

    public TitanFileChannelChanface(FileChannel channel ){
        this.channel = channel;
        this.reentrantLock = new ReentrantLock();
    }

    @Override
    public void position(long position) throws IOException {
        this.channel.position( position );
    }

    @Override
    public int read( ByteBuffer buffer ) throws IOException {
        return this.channel.read( buffer );
    }

    @Override
    public int read( ChanfaceReader reader, int size, long offset ) throws IOException {
        this.reentrantLock.lock();
        int read = 0;
        try {
            this.channel.position( offset );
            ByteBuffer buffer = ByteBuffer.allocateDirect(size);
            read = this.channel.read(buffer);
            reader.afterRead( buffer );
        }
        finally {
            this.reentrantLock.unlock();
        }
        return read;
    }

    @Override
    public int write( ByteBuffer buffer ) throws IOException {
        return this.channel.write( buffer );
    }

    @Override
    public int write(byte[] buffer, int startPosition, int endSize) throws IOException {
        ByteBuffer byteBuffer = this.copyToTemporaryBuffer(buffer, startPosition, endSize);
        return this.channel.write( byteBuffer );
    }

    @Override
    public int write(byte[] buffer, List<CacheBlock> writableCacheBlocks) throws IOException {
        ByteBuffer byteBuffer = this.mergeArrays(buffer, writableCacheBlocks);
        return this.channel.write(byteBuffer);
    }

    @Override
    public long position() throws IOException {
        return this.channel.position();
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

    private ByteBuffer copyToTemporaryBuffer(byte[] buffer, int startPosition, int endSize ){
        ByteBuffer temporaryBuffer = ByteBuffer.allocate( endSize );
        temporaryBuffer.put( buffer, startPosition, endSize );
        return temporaryBuffer;
    }

    private ByteBuffer mergeArrays( byte[] buffer, List<CacheBlock> writableCacheBlocks ){
        // 计算所有缓存块的总长度
        int totalLength = 0;
        for (CacheBlock cacheBlock : writableCacheBlocks) {
            totalLength += cacheBlock.getValidByteEnd().intValue() - cacheBlock.getValidByteStart().intValue();
        }

        // 创建一个 ByteBuffer 来存储合并的数据
        ByteBuffer mergedBuffer = ByteBuffer.allocate(totalLength);

        // 将数据从 mBuffer 复制到 mergedBuffer
        for (CacheBlock cacheBlock : writableCacheBlocks) {
            int start = cacheBlock.getValidByteStart().intValue();
            int end = cacheBlock.getValidByteEnd().intValue();
            int bufferSize = end - start;

            // 将 mBuffer 中的数据复制到 mergedBuffer
            mergedBuffer.put(buffer, start, bufferSize);

            // 将缓存块状态设置为 Free
            cacheBlock.setStatus(CacheBlockStatus.Free);
        }
        // 准备将 mergedBuffer 用于读取
        mergedBuffer.flip();
        return mergedBuffer;
    }
}
