package com.pinecone.framework.system.executum;


import com.pinecone.framework.system.ApoptosisRejectSignalException;
import com.pinecone.framework.system.GenericMasterTaskManager;

import java.util.Map;

public abstract class ArchProcessum extends ArchExecutum implements Processum {
    protected GenericMasterTaskManager mTaskManager              ;

    public ArchProcessum ( String szName, Processum parent ) {
        super( szName, parent );
    }


    @Override
    public ArchProcessum applyExceptionRestartTime( int time ){
        return (ArchProcessum) super.applyExceptionRestartTime( time );
    }

    @Override
    public ArchProcessum setThreadAffinity( Thread affinity ) {
        return (ArchProcessum) super.setThreadAffinity( affinity );
    }

    @Override
    public void  apoptosis() {
        this.interrupt();
    }

    @Override
    public void interrupt() {
        if( this.getAffiliateThread() != null ) {
            this.getAffiliateThread().interrupt();
        }
    }

    @Override
    public void  kill() {
        this.getTaskManager().terminate();
        if( this.getAffiliateThread() != null ) {
            this.getAffiliateThread().stop();
        }
    }

    @Override
    public void  suspend() {
        this.getAffiliateThread().suspend();
    }

    @Override
    public void  resume() {
        this.getAffiliateThread().resume();
    }

    @Override
    public void  entreatLive() {
        throw new ApoptosisRejectSignalException();
    }

    @Override
    public GenericMasterTaskManager getTaskManager() {
        return this.mTaskManager;
    }

    @Override
    public Map<Integer, Executum > getOwnThreadGroup() {
        return this.getTaskManager().getExecutumPool();
    }
}
