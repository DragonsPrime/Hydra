package com.pinecone.hydra.storage.volume.runtime;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.lock.SpinLock;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;

public class MasterVolumeGram extends ArchProcessum implements VolumeGram {
    protected SpinLock mMajorStatusIO = new SpinLock();
    protected int                   jobNum;
    protected List<Object>          lockGroup;
    protected List<CacheBlock>      cacheGroup;
    protected byte[]                buffer;

    public MasterVolumeGram( String szName, Processum parent ) {
        super( szName, parent );
        this.mTaskManager      = new GenericMasterTaskManager( this );
    }

    public MasterVolumeGram( String szName, Processum parent, int jobNum, List<Object> lockGroup, List<CacheBlock> cacheGroup, byte[] buffer ){
        super( szName, parent );
        this.mTaskManager   = new GenericMasterTaskManager( this );
        this.jobNum         = jobNum;
        this.lockGroup      = lockGroup;
        this.cacheGroup     = cacheGroup;
        this.buffer         = buffer;
    }

    public SpinLock getMajorStatusIO() {
        return this.mMajorStatusIO;
    }

    public LocalStripedTaskThread getChildThread( int threadId ){
        return (LocalStripedTaskThread) this.getTaskManager().getExecutumPool().get( threadId );
    }


    @Override
    public int getJobNum() {
        return this.jobNum;
    }

    @Override
    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
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
