package com.pinecone.hydra.storage.volume.entity.local.striped;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import com.pinecone.framework.util.Debug;

public class TitanStripLockEntity implements StripLockEntity{
    private Semaphore     bufferToFileLock;

    private Object        lockObject;


    public TitanStripLockEntity(){}

    public TitanStripLockEntity( Object lockObject, Semaphore bufferToFileLock ){
        this.lockObject = lockObject;
        this.bufferToFileLock  = bufferToFileLock;
    }

    @Override
    public Object getLockObject() {
        return this.lockObject;
    }

    @Override
    public void setLockObject(Object lockObject) {
        this.lockObject = lockObject;
    }


    @Override
    public Semaphore getBufferToFileLock() {
        return this.bufferToFileLock;
    }

    @Override
    public void unlockBufferToFileLock() {
        this.bufferToFileLock.release();
    }
}
