package com.pinecone.hydra.storage.volume.entity.local.striped;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import com.pinecone.framework.util.Debug;

public class TitanStripLockEntity implements StripLockEntity{
    private Object        lockObject;
    private List< Object> lockGroup;
    private AtomicInteger currentBufferCode;
    private Lock          maoLock;

    public TitanStripLockEntity(){}

    public TitanStripLockEntity( Object lockObject, List<Object> lockGroup, AtomicInteger currentBufferCode, Lock maoLock ){
        this.lockObject = lockObject;
        this.lockGroup = lockGroup;
        this.currentBufferCode = currentBufferCode;
        this.maoLock = maoLock;
    }

    @Override
    public Object getLockObject() {
        return this.lockObject;
    }

    @Override
    public void setLockObject(Object lockObject) {
        this.lockObject = lockObject;
    }

    public Lock getMaoLock() {
        return maoLock;
    }

    public void setMaoLock(Lock maoLock) {
        this.maoLock = maoLock;
    }

    @Override
    public List<Object> getLockGroup() {
        return this.lockGroup;
    }

    @Override
    public void setLockGroup(List<Object> lockGroup) {
        this.lockGroup = lockGroup;
    }

    @Override
    public AtomicInteger getCurrentBufferCode() {
        return this.currentBufferCode;
    }

    @Override
    public void setCurrentBufferCode(AtomicInteger currentBufferCode) {
        this.currentBufferCode = currentBufferCode;
    }

    @Override
    public void unlockPipeStage() {
        for( final Object lockObject : this.getLockGroup() ){
            synchronized ( lockObject ){
                lockObject.notify();
            }
        }
    }
}
