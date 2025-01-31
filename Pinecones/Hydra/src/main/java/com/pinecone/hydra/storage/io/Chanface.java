package com.pinecone.hydra.storage.io;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public interface Chanface extends Pinenut {
    void position( long position ) throws IOException;

    int read( ByteBuffer buffer ) throws IOException;

    int read( ChanfaceReader reader, int size, long offset ) throws IOException;

    int read( byte[] buffer, int size, long offset ) throws IOException;

//    int read( ChanfaceReader reader, int size, long offset ) throws IOException;

    int write( ByteBuffer buffer ) throws IOException;

    int write( byte[] buffer, int startPosition, int endSize ) throws IOException;

    int write( byte[] buffer, List<CacheBlock> writableCacheBlocks ) throws IOException;

    long position() throws IOException;

    void close() throws IOException;

    Object getNativeFace();

}
