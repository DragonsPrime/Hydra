package com.pinecone.hydra.storage.volume.runtime;

import java.util.concurrent.locks.ReentrantLock;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;

public class MasterVolumeGram extends ArchProcessum implements VolumeGram {
    protected ReentrantLock mMajorStatusIO = new ReentrantLock();

    public MasterVolumeGram( String szName, Processum parent ) {
        super( szName, parent );

        this.mTaskManager      = new GenericMasterTaskManager( this );
    }

    public ReentrantLock getMajorStatusIO() {
        return this.mMajorStatusIO;
    }

    public LocalStripedTaskThread getChildThread( int threadId ){
        return (LocalStripedTaskThread) this.getTaskManager().getExecutumPool().get( threadId );
    }


}
