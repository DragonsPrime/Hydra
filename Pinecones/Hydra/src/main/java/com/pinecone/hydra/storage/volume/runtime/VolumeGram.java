package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.util.List;

public interface VolumeGram extends Processum {
    int getJobCount();
    void setJobCount( int jobCount );

    List<CacheBlock> getCacheGroup();
    void setCacheGroup( List<CacheBlock> cacheGroup );

    byte[] getBuffer();
    void setBuffer( byte[] buffer );

}
