package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJobCompromiseException;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TitanStripReceiveBufferOutJob implements StripReceiveBufferOutJob{
    protected MasterVolumeGram          masterVolumeGram;

    protected byte[]                    buffer;

    protected StripBufferStatus         status;

    protected InputStream               stream;

    protected final Semaphore           blockerLatch;

    protected List< CacheBlock >        cacheBlocksGroup;

    protected LocalStripedTaskThread    parentThread;

    protected VolumeManager             volumeManager;

    protected long                      totalSize;

    protected long                      exportSize;

    protected int                       currentBufferInJobCode;


    public TitanStripReceiveBufferOutJob(MasterVolumeGram masterVolumeGram,VolumeManager volumeManager, long totalSize, InputStream stream ){
        this.masterVolumeGram  = masterVolumeGram;
        this.stream            = stream;
        this.totalSize         = totalSize;
        this.volumeManager     = volumeManager;
        this.blockerLatch      = new Semaphore(0);
        this.masterVolumeGram.applyBufferOutBlockerLatch( this.blockerLatch );
        this.exportSize        = 0;
        this.cacheBlocksGroup  = this.masterVolumeGram.getCacheGroup();
    }
    @Override
    public void execute() throws VolumeJobCompromiseException {

    }

    @Override
    public void applyThread(LocalStripedTaskThread thread) {
        this.parentThread = thread;
    }

    @Override
    public StripBufferStatus getStatus() {
        return this.status;
    }

    @Override
    public Semaphore getBlockerLatch() {
        return this.blockerLatch;
    }

    @Override
    public void setStatus(StripBufferStatus status) {
        this.status = status;
    }
}
