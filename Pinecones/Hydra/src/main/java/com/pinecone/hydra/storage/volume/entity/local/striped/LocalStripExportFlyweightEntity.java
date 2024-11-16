package com.pinecone.hydra.storage.volume.entity.local.striped;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalStripExportFlyweightEntity implements StripExportFlyweightEntity {
    private int                         jobCount;
    private int                         jobCode;
    private StripLockEntity             lockEntity;
    private byte[]                      buffer;
    private List<CacheBlock>            cacheBlockGroup;
    private int                         bufferToFileThreadId;

    public LocalStripExportFlyweightEntity(){}
    public LocalStripExportFlyweightEntity( int jobCount, int jobCode, StripLockEntity lockEntity ){
        this.jobCount                     = jobCount;
        this.jobCode                    = jobCode;
        this.lockEntity                 = lockEntity;
    }

    public LocalStripExportFlyweightEntity( StripLockEntity lockEntity ){
        this.lockEntity = lockEntity;
    }


    @Override
    public int getJobCount() {
        return this.jobCount;
    }

    @Override
    public void setJobCount(int jobCount) {
        this.jobCount = jobCount;
    }

    @Override
    public int getJobCode() {
        return this.jobCode;
    }

    @Override
    public void setJobCode(int jobCode) {
        this.jobCode = jobCode;
    }

    @Override
    public StripLockEntity getLockEntity() {
        return this.lockEntity;
    }

    @Override
    public void setLockEntity(StripLockEntity lockEntity) {
        this.lockEntity = lockEntity;
    }

    @Override
    public byte[] getBuffer() {
        return this.buffer;
    }

    @Override
    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    @Override
    public List<CacheBlock> getCacheBlockGroup() {
        return this.cacheBlockGroup;
    }

    @Override
    public void setCacheBlockGroup(List<CacheBlock> cacheBlockGroup) {
        this.cacheBlockGroup = cacheBlockGroup;
    }

    @Override
    public int getBufferToFileThreadId() {
        return this.bufferToFileThreadId;
    }

    @Override
    public void setBufferToFileThreadId(int bufferToFileThreadId) {
        this.bufferToFileThreadId = bufferToFileThreadId;
    }
}
