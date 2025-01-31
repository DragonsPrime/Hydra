package com.pinecone.hydra.storage.io;

import com.pinecone.framework.system.NotImplementedException;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlockStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TitanOutputStreamChanface implements Chanface {
    protected OutputStream  stream;

    protected final ReentrantLock reentrantLock;

    public TitanOutputStreamChanface(OutputStream stream ){
        this.stream = stream;
        this.reentrantLock = new ReentrantLock();
    }

    @Override
    public void position(long position) throws IOException {

    }

    @Override
    public int read( ByteBuffer buffer ) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public int read(ChanfaceReader reader, int size, long offset ) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public int read(byte[] buffer, int size, long offset) throws IOException {
        return 0;
    }

    @Override
    public int write(ByteBuffer buffer) throws IOException {
        return this.writeToByte( buffer );
    }

    @Override
    public int write(byte[] buffer, int startPosition, int endSize) throws IOException {
        this.stream.write( buffer, startPosition, endSize );
        return endSize;
    }

    @Override
    public int write(byte[] buffer, List<CacheBlock> writableCacheBlocks) throws IOException {
        int length = 0;

        for( CacheBlock cacheBlock : writableCacheBlocks ){
            length += ( cacheBlock.getValidByteEnd().intValue() - cacheBlock.getValidByteStart().intValue() );
            this.stream.write( buffer, cacheBlock.getValidByteStart().intValue(), cacheBlock.getValidByteEnd().intValue() - cacheBlock.getValidByteStart().intValue() );
            cacheBlock.setStatus( CacheBlockStatus.Free );
        }
        return length;
    }

    @Override
    public long position() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }


    private int writeToByte(ByteBuffer buffer) throws IOException {
        if (buffer == null) {
            throw new NullPointerException("Buffer is null");
        }

        int bytesWritten = 0;
        byte[] tempArray = new byte[buffer.remaining()];
        buffer.get(tempArray);
        this.stream.write(tempArray);
        bytesWritten = tempArray.length;

        return bytesWritten;
    }


    @Override
    public Object getNativeFace() {
        return this.stream;
    }
}
