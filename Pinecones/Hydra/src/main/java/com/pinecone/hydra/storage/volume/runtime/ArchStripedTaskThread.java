package com.pinecone.hydra.storage.volume.runtime;

import java.util.concurrent.Semaphore;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.executum.Processum;

public abstract class ArchStripedTaskThread extends ArchTaskThread implements Runnable {
    protected VolumeJob    mVolumeJob;

    protected ArchStripedTaskThread ( String szName, Processum parent, VolumeJob volumeJob ) {
        super( szName, parent );
        this.mVolumeJob = volumeJob;

        Thread affinityThread = new Thread( this );
        affinityThread.setDaemon(false);

        this.setThreadAffinity( affinityThread );
        this.getAffiliateThread().setName( szName );
        this.setName( affinityThread.getName() );
    }


    protected void executeSingleJob() throws VolumeJobCompromiseException {
        this.mVolumeJob.execute();
    }

    @Override
    public void run() {
        //switch ()
        try{
            this.executeSingleJob();
        }
        catch ( VolumeJobCompromiseException e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }
}
