package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

public interface CacheBlock extends Pinenut { ;

    CacheBlockStatus getStatus();
    void setStatus( CacheBlockStatus status );

    Number getValidByteStart();
    void setValidByteStart( Number validByteStart );

    Number getValidByteEnd();
    void setValidByteEnd( Number validByteEnd );

    Number getByteStart();
    void setByteStart( Number byteStart );

    Number getByteEnd();
    void setByteEnd( Number byteEnd );

    int getCacheBlockNumber();
    void setCacheBlockNumber( int cacheBlockNumber );

    int getBufferWriteThreadId();
    void setBufferWriteThreadId( int bufferWriteThreadId );
}
