package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;

public class MasterVolumeGram extends ArchProcessum implements VolumeGram {
    public MasterVolumeGram( String szName, Processum parent ) {
        super( szName, parent );

        this.mTaskManager      = new GenericMasterTaskManager( this );
    }

}
