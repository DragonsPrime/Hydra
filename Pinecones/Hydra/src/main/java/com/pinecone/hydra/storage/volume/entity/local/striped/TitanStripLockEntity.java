package com.pinecone.hydra.storage.volume.entity.local.striped;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanStripLockEntity implements StripLockEntity{
    private Object        lockObject;
    private List< Object> lockGroup;
    private AtomicInteger currentBufferCode;

    public TitanStripLockEntity(){}

    public TitanStripLockEntity( Object lockObject, List<Object> lockGroup, AtomicInteger currentBufferCode ){
        this.lockObject = lockObject;
        this.lockGroup = lockGroup;
        this.currentBufferCode = currentBufferCode;
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
}
