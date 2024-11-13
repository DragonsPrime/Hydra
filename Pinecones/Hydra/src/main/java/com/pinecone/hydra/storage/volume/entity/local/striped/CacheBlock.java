package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

public interface CacheBlock extends Pinenut {
    byte[] getCache();
    void setCache( byte[] cache );

    CacheBlockStatus getStatus();
    void setStatus( CacheBlockStatus status );

    Number getValidByteStart();
    void setValidByteStart( Number validByteStart );

    Number getValidByteEnd();
    void setValidByteEnd( Number validByteEnd );

    int getCacheBlockNumber();
    void setCacheBlockNumber( int cacheBlockNumber );
}
