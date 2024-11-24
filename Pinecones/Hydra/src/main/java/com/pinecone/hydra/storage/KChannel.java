package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public interface KChannel extends Pinenut {
    void position( long position ) throws IOException;
    int read( ByteBuffer buffer ) throws IOException;
    int read( ByteBuffer buffer, long offset ) throws IOException;

    int write( ByteBuffer buffer ) throws IOException;

    int write( byte[] buffer, int startPosition, int endSize ) throws IOException;

    int write(byte[] buffer, List<CacheBlock> writableCacheBlocks ) throws IOException;

    long position() throws IOException;

    void close() throws IOException;
}
