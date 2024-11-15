package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface StripExportFlyweightEntity extends Pinenut {

    int getJobNum();
    void setJobNum( int jobNum );

    int getJobCode();
    void setJobCode( int jobCode );

    StripLockEntity getLockEntity();
    void setLockEntity( StripLockEntity lockEntity );

    byte[] getBuffer();
    void setBuffer( byte[] buffer );

    List< CacheBlock> getCacheBlockGroup();
    void setCacheBlockGroup( List< CacheBlock> cacheBlockGroup );

    int getBufferToFileThreadId();
    void setBufferToFileThreadId( int bufferToFileThreadId );

}
