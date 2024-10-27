package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.executum.Processum;

public abstract class ArchStripedTaskThread extends ArchTaskThread implements Runnable {
    protected VolumeJob mVolumeJob;

    protected ArchStripedTaskThread ( String szName, Processum parent, VolumeJob volumeJob ) {
        super( szName, parent );
        this.mVolumeJob = volumeJob;

        Thread affinityThread = new Thread( this );
        this.setThreadAffinity( affinityThread );
        this.getAffiliateThread().setName( szName );
        this.setName( affinityThread.getName() );
    }

    protected void executeSingleJob() {
        this.mVolumeJob.execute();
    }

    @Override
    public void run() {
        //switch ()
        this.executeSingleJob();
    }
}
