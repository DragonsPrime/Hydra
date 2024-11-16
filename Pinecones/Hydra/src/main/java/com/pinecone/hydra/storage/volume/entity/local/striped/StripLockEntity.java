package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public interface StripLockEntity extends Pinenut {
    Object getLockObject();

    void setLockObject( Object lockObject );

    void unlockBufferToFileLock();

    Semaphore getBufferToFileLock();

}
