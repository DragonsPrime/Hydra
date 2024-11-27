package com.pinecone.hydra.storage;

import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TitanInputStreamChannel implements KChannel{
    protected InputStream          stream;

    protected final ReentrantLock  reentrantLock;

    public TitanInputStreamChannel( InputStream stream ){
        this.stream        = stream;
        this.reentrantLock = new ReentrantLock();
    }

    @Override
    public void position(long position) throws IOException {
        this.stream.skip( position );
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException {
        byte[] tempBuffer = new byte[buffer.remaining()];
        int bytesRead = stream.read(tempBuffer);
        buffer.put(tempBuffer, 0, bytesRead);
        return bytesRead;
    }

    @Override
    public int read(ByteBuffer buffer, long offset) throws IOException {

        return 0;
    }

    @Override
    public int write(ByteBuffer buffer) throws IOException {
        return 0;
    }

    @Override
    public int write(byte[] buffer, int startPosition, int endSize) throws IOException {
        return 0;
    }

    @Override
    public int write(byte[] buffer, List<CacheBlock> writableCacheBlocks) throws IOException {
        return 0;
    }

    @Override
    public long position() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
