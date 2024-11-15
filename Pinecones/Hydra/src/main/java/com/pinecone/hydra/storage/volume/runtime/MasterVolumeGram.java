package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.lock.SpinLock;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;

public class MasterVolumeGram extends ArchProcessum implements VolumeGram {
    protected SpinLock mMajorStatusIO = new SpinLock();

    public MasterVolumeGram( String szName, Processum parent ) {
        super( szName, parent );

        this.mTaskManager      = new GenericMasterTaskManager( this );
    }

    public SpinLock getMajorStatusIO() {
        return this.mMajorStatusIO;
    }

    public LocalStripedTaskThread getChildThread( int threadId ){
        return (LocalStripedTaskThread) this.getTaskManager().getExecutumPool().get( threadId );
    }


}
