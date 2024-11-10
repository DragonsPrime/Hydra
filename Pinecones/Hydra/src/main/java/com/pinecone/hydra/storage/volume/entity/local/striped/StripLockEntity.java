package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public interface StripLockEntity extends Pinenut {
    Object getLockObject();

    void setLockObject( Object lockObject );

    List<Object> getLockGroup();

    void setLockGroup( List<Object> lockGroup );

    AtomicInteger getCurrentBufferCode();

    void setCurrentBufferCode( AtomicInteger currentBufferCode );

    void unlockPipeStage();

    Lock getMaoLock() ;
}
