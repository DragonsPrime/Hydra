package com.pinecone.hydra.servgram;

import com.pinecone.framework.util.config.PatriarchalConfig;
import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.system.component.Slf4jTraceable;
import org.slf4j.Logger;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ArchServgramium extends ArchProcessum implements Servgramium, Slf4jTraceable {
    protected Logger                 mLogger;
    protected String                 mszGramName;

    protected PatriarchalConfig      mServgramList;
    protected PatriarchalConfig      mServgramConf;
    protected boolean                mbTraceLifecycle;

    public ArchServgramium( String szGramName, Processum parent ) {
        super( szGramName, parent );
        this.mszGramName     = szGramName;
        this.mTaskManager    = new GenericMasterTaskManager( this );
        this.mLogger         = this.getSystem().getTracerScope().newLogger( this.className() );
        this.loadConfig();
        this.infoLifecycle( "MeeseekSpawned", "I'm Mr.Meeseek[" + this.className() + "], look at me !" );
    }

    protected ArchServgramium( Servgramium shared, boolean bs ) {
        super( shared.getName(), (Processum) shared.parentExecutum() );
        ArchServgramium that = (ArchServgramium) shared;
        this.mszGramName     = that.mszGramName;
        this.mTaskManager    = that.mTaskManager;
        this.mLogger         = that.mLogger;
        this.mServgramList   = that.mServgramList;
        this.mServgramConf   = that.mServgramConf;
    }

    @Override
    public Logger getLogger() {
        return this.mLogger;
    }

    @Override
    public ArchServgramium infoLifecycle( String szWhat, String szStateOrExtra ) {
        this.getLogger().info( "[Lifecycle] [{}] <{}>", szWhat, szStateOrExtra );
        return this;
    }

    @Override
    public ArchServgramium infoLifecycle( String szStateOrExtra ) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        return this.infoLifecycle( stackTraceElements[ 2 ].getMethodName(), szStateOrExtra );
    }

    protected void loadConfig() {
        this.mServgramList     = this.getAttachedOrchestrator().getSectionConfig().getChild( Servgram.ConfigServgramsKey );
        Object dyServgramConf  = this.mServgramList.get( this.gramName() );
        if( dyServgramConf instanceof String ) {
            try{
                this.mServgramConf = this.mServgramList.getChildFromPath( Path.of((String) dyServgramConf) );
            }
            catch ( IOException e ) {
                this.getSystem().handleKillException( e );
            }
        }
        else {
            this.mServgramConf = this.mServgramList.getChild( this.gramName() );
        }

        //Debug.trace( this.mMeeseekConf );
    }

    public boolean isTraceLifecycle() {
        return this.mbTraceLifecycle;
    }

    @Override
    public ServgramOrchestrator getAttachedOrchestrator() {
        return (ServgramOrchestrator) this.parentExecutum().getTaskManager();
    }

    @Override
    public String       gramName() {
        return this.mszGramName;
    }

    @Override
    public Hydrarum getSystem() {
        return (Hydrarum) super.getSystem();
    }

    @Override
    public PatriarchalConfig getConfig() {
        return this.mServgramConf;
    }

    @Override
    public abstract void execute() throws Exception;
}
