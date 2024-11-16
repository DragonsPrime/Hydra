package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.framework.system.RedirectRuntimeException;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public abstract class WolfNettyServgram extends ArchServgramium {
    protected JSONObject                mjoSectionConf;
    protected final Object              mPrimaryThreadJoinMutex         = new Object(); // Joining the primary thread, waiting for client-sub-system terminated.
    protected final Object              mOuterThreadDetachMutex         = new Object(); // Waiting for primary thread initialized. [Outer refers invoked thead, e.g. Usually main-thread]

    protected ReentrantLock             mStateMutex                     = new ReentrantLock();
    protected boolean                   mShutdown                       = true ;

    public WolfNettyServgram( String szName, Hydrarum system, Map<String, Object> joConf ) {
        super( szName, system );

        if( joConf instanceof JSONObject ) {
            this.mjoSectionConf = (JSONObject) joConf;
        }
        else {
            this.mjoSectionConf = new JSONMaptron( joConf, true );
        }
    }

    public JSONObject getSectionConf() {
        return this.mjoSectionConf;
    }

    @Override
    public Hydrarum getSystem() {
        return (Hydrarum) super.getSystem();
    }



    public boolean isShutdown(){
        return this.mShutdown;
    }

    @Override
    public boolean isTerminated(){
        return this.isShutdown();
    }


    protected void unlockOuterThreadDetachMutex() {
        synchronized ( this.mOuterThreadDetachMutex ) {
            this.mOuterThreadDetachMutex.notify();
        }
    }

    protected void preparePrimaryThread( Thread primaryThread ) {
        primaryThread.setName( ( this.className() + "-primary-" + primaryThread.getName() ).toLowerCase() );
        this.setThreadAffinity( primaryThread );
    }

    protected void joinOuterThread() {
        synchronized ( this.mOuterThreadDetachMutex ) {
            try {
                this.mOuterThreadDetachMutex.wait();// Waiting for primary thread initialized.
                // This mutex will not locks the parent thread, if you wish to lock it, adding more locks.
                // If primary thread successfully executed, do nothing, and goto back to parent thread.
                // If primary exception thrown, redirected it to parent thread.
            }
            catch ( InterruptedException e ) {
                this.getSystem().handleLiveException( e );
            }
        }
    }

    protected void redirectIOException2ParentThread( Exception previousException ) throws IOException {
        if( previousException instanceof RuntimeException ) {
            throw new RedirectRuntimeException( previousException );
        }
        else if( previousException instanceof IOException) {
            throw new IOException( previousException.getMessage(), previousException );
        }
        else if( previousException != null ){
            this.getSystem().handleKillException( previousException ); // This should never be happened.
        }
    }



    @Override
    public String toString() {
        return String.format(
                "[object %s(0x%s)<\uD83D\uDC3A>]",
                this.className() , Integer.toHexString( this.hashCode() )
        );
    }

    @Override
    public String toJSONString() {
        return "\"" + this.toString() + "\"";
    }
}
