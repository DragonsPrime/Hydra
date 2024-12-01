package com.pinecone.hydra.storage.volume.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.lock.SpinLock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripCacheBlock;

public class MasterVolumeGram extends ArchProcessum implements VolumeGram {
    protected Lock                  mMajorStatusIO = new SpinLock();
    protected int                   jobCount;
    protected int                   bufferOutThreadId;
    protected Semaphore             bufferOutBlockerLatch;
    protected int                   currentBufferInJobCode;
    
    
    protected List<CacheBlock>      cacheGroup;
    protected byte[]                buffer;

    public MasterVolumeGram( String szName, Processum parent ) {
        super( szName, parent );
        this.mTaskManager      = new GenericMasterTaskManager( this );
    }

    public MasterVolumeGram( String szName, Processum parent, int jobCount, int StripResidentCacheAllotRatio, int stripSize ){
        super( szName, parent );
        this.mTaskManager   = new GenericMasterTaskManager( this );
        this.jobCount       = jobCount;
        this.cacheGroup     = this.initializeCacheGroup( jobCount, StripResidentCacheAllotRatio, stripSize );
        this.buffer         = new byte[ jobCount * stripSize * StripResidentCacheAllotRatio ];
        this.currentBufferInJobCode = 0;
    }

    public Lock getMajorStatusIO() {
        return this.mMajorStatusIO;
    }

    public LocalStripedTaskThread getChildThread( int threadId ){
        return (LocalStripedTaskThread) this.getTaskManager().getExecutumPool().get( threadId );
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
    public List<CacheBlock> getCacheGroup() {
        return this.cacheGroup;
    }

    @Override
    public void setCacheGroup(List<CacheBlock> cacheGroup) {
        this.cacheGroup = cacheGroup;
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
    public int getBufferOutThreadId() {
        return this.bufferOutThreadId;
    }

    @Override
    public void applyBufferOutThreadId(int bufferOutThreadId) {
        this.bufferOutThreadId = bufferOutThreadId;
    }

    @Override
    public void applyBufferOutBlockerLatch(Semaphore bufferOutBlockerLatch) {
        this.bufferOutBlockerLatch = bufferOutBlockerLatch;
    }

    @Override
    public Semaphore getBufferOutBlockerLatch() {
        return this.bufferOutBlockerLatch;
    }

    @Override
    public int getCurrentBufferInJobCode() {
        return this.currentBufferInJobCode;
    }

    @Override
    public void setCurrentBufferInJobCode(int currentBufferInJobCode) {
        this.currentBufferInJobCode = currentBufferInJobCode;
    }

    private List< CacheBlock > initializeCacheGroup(int jobCount, int StripResidentCacheAllotRatio, Number stripSize ){
        ArrayList<CacheBlock> cacheGroup = new ArrayList<>();
        Number currentPosition = 0;
        for( int i = 0; i < jobCount * StripResidentCacheAllotRatio; i++ ){
            StripCacheBlock stripCacheBlock = new StripCacheBlock( i, currentPosition, currentPosition.intValue() + stripSize.intValue() );
            cacheGroup.add( stripCacheBlock );
            currentPosition = currentPosition.intValue() + stripSize.intValue();
        }
        return  cacheGroup;
    }
}
