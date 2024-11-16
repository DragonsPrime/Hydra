package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.storage.volume.runtime.ArchStripedTaskThread;
import com.pinecone.hydra.storage.volume.runtime.VolumeJob;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalStripedTaskThread extends ArchStripedTaskThread {

    public LocalStripedTaskThread ( String szName, Processum parent, VolumeJob volumeJob ) {
        super( szName, parent, volumeJob );

        volumeJob.applyThread( this );
    }

    StripBufferStatus getJobStatus(){
        return this.mVolumeJob.getStatus();
    }

    void setJobStatus( StripBufferStatus status ){
        this.mVolumeJob.setStatus( status );
    }

    Semaphore getBlockerLatch(){
        return this.mVolumeJob.getBlockerLatch();
    }
}
