package com.pinecone.hydra.storage.volume.runtime;
import com.pinecone.framework.system.executum.ArchThreadum;
import com.pinecone.framework.system.executum.Processum;

public abstract class ArchTaskThread extends ArchThreadum {
    protected ArchTaskThread ( String szName, Processum parent ) {
        super( szName, parent, null );
    }

    public void start() {
        if( this.getAffiliateThread() != null ) {
            this.getAffiliateThread().start();
        }
    }
}
