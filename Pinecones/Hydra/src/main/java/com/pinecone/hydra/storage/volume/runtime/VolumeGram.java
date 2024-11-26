package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.util.List;
import java.util.concurrent.Semaphore;

public interface VolumeGram extends Processum {
    int getJobCount();
    void setJobCount( int jobCount );

    List<CacheBlock> getCacheGroup();
    void setCacheGroup( List<CacheBlock> cacheGroup );

    byte[] getBuffer();
    void setBuffer( byte[] buffer );

    int getBufferOutThreadId();
    void applyBufferOutThreadId(int bufferOutThreadId );

    void applyBufferOutBlockerLatch( Semaphore bufferOutBlockerLatch);
    Semaphore getBufferOutBlockerLatch();

    int getCurrentBufferInJobCode();
    void setCurrentBufferInJobCode( int currentBufferInJobCode );
}
