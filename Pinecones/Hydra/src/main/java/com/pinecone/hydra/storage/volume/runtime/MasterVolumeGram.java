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

public class MasterVolumeGram extends ArchProcessum implements VolumeGram {
    protected Lock                  mMajorStatusIO = new SpinLock();
    protected int                   jobCount;
    
    
    
    protected List<CacheBlock>      cacheGroup;
    protected byte[]                buffer;

    public MasterVolumeGram( String szName, Processum parent ) {
        super( szName, parent );
        this.mTaskManager      = new GenericMasterTaskManager( this );
    }

    public MasterVolumeGram( String szName, Processum parent, int jobCount, List<CacheBlock> cacheGroup, byte[] buffer ){
        super( szName, parent );
        this.mTaskManager   = new GenericMasterTaskManager( this );
        this.jobCount         = jobCount;
        this.cacheGroup     = cacheGroup;
        this.buffer         = buffer;
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
}
