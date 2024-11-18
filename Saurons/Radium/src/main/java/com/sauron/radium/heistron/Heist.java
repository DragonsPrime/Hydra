package com.sauron.radium.heistron;

//import com.genius.common.Heist;
//import com.genius.common.UlfUMC.UlfUMCMessage;
//import com.genius.config.HeistConfig;
//import com.genius.config.SystemConfig;
//import com.genius.mq.Harbor;
//import com.genius.pool.FunctionNamePool;
//import org.springframework.beans.factory.annotation.Autowired;

import com.pinecone.framework.system.NonNull;
import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.json.JSONArray;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.servgram.GramTransaction;
import com.pinecone.hydra.servgram.OrchestrateInterruptException;
import com.pinecone.hydra.servgram.Orchestrator;
import com.pinecone.hydra.servgram.ServgramOrchestrator;
import com.sauron.radium.heistron.orchestration.ChildHeistOrchestrator;
import com.sauron.radium.heistron.orchestration.HeistletOrchestrator;
import com.sauron.radium.heistron.orchestration.Hierarchy;
import com.sauron.radium.heistron.scheduler.RangedTaskPage;
import com.pinecone.framework.util.config.JSONConfig;
import com.sauron.radium.util.ConfigHelper;
import com.pinecone.framework.util.json.homotype.DirectObjectInjector;
import com.pinecone.slime.chunk.RangedPage;


/**
 * 负责抢劫任务(Heist)的任务调度分配
 */
public abstract class Heist extends ArchHeistum implements CascadeHeist {
    //@Autowired
    //private Harbor harbor;  //港口，负责和master结点进行通信

    protected Hierarchy         mHierarchy;
    protected Namespace         mHeistName;
    protected CascadeHeist      mParent          = null;

    protected JSONConfig        mjoProtoConfig ;  // 当前Heist的JSON原型配置项，等待被子Heist继承和重写
    protected JSONConfig        mjoInstanceConfig;

    protected HeistScheme       heistScheme;

    protected RangedPage        mMasterHeistTaskPage;
    protected Heistium          mHeistium;       // The affiliated process, which is using for executing specific heist.

    protected Heist( Heistgram heistgram, @Nullable JSONConfig joConfig, @Nullable CascadeHeist parent, Namespace heistName ) {
        super( heistgram );
        this.mParent     = parent;
        this.mHeistName  = heistName;

        this.infoLifecycle( "Heist::FinalConstructor", "A new heist has been contrived" );
        this.apply( joConfig );
    }

    protected Heist( Heistgram heistron, @Nullable JSONConfig joConfig, @Nullable CascadeHeist parent, @Nullable String szHeistName ) {
        this( heistron, joConfig, parent, (Namespace) null );
        if( szHeistName == null ) {
            szHeistName = heistron.searchHeistName( this );
        }
        this.mHeistName = CascadeHeist.newNamespace( szHeistName, parent );
    }

    public Heist( Heistgram heistron, @Nullable CascadeHeist parent, @NonNull String szChildName ) {
        this( heistron, null, parent, szChildName );

        JSONConfig jc = parent.getHeistScheme().getInstanceConfigByName( szChildName, true );
        parent.getHeistScheme().reinterpret( jc );
        this.apply( jc );
    }

    public Heist( Heistgram heistron, @Nullable JSONConfig joConfig ){
        this( heistron, joConfig, null, (String) null );
    }

    public Heist( Heistgram heistron ) {
        this( heistron, (String) null );
    }

    public Heist( Heistgram heistron, @Nullable String szName ) {
        this( heistron, null, null, szName );

        if( this.mHeistName != null ) {
            this.apply( heistron.queryHeistConfig( this.heistName() ) );
        }
    }

    protected void loadConfig() {
        this.applyThisInnerConfig( HeistEntity.class );
        this.apply( this.taskFrom, this.taskTo );
    }

    public Heist apply( long taskFrom, long taskTo ) {
        this.taskFrom             = taskFrom;
        this.taskTo               = taskTo;

        this.mMasterHeistTaskPage = new RangedTaskPage( taskFrom, taskTo, 0 );
        this.mHeistium            = new LocalHeistium( this, 0 );
        return this;
    }

    public Heist apply( @Nullable JSONConfig joProtoConfig ) {
        if( joProtoConfig != null ) {
            this.mjoProtoConfig = joProtoConfig;
            this.heistScheme    = new HeistScheme( this );

            this.mjoInstanceConfig = this.getHeistScheme().getInstanceConfigByName( null, true );
            this.getHeistScheme().reinterpret( this.mjoInstanceConfig );

            //Debug.fmt( 2, this.mjoInstanceConfig );

            this.mHierarchy = this.queryHeistHierarchy();
            this.loadConfig();
            this.reportScheme();
        }
        return this;
    }

    protected Hierarchy queryHeistHierarchy() {
        JSONConfig joOrchestration = this.getConfig().getChild( Orchestrator.ConfigOrchestrationKey );
        if( joOrchestration != null ) {
            JSONArray transaction = joOrchestration.optJSONArray( GramTransaction.ConfigTransactionsListKey );
            if( transaction != null && !transaction.isEmpty() ) {
                return Hierarchy.Master;
            }
        }
        return Hierarchy.Slave; // Root is also a slave.
    }

    protected void applyThisInnerConfig( Class<?> stereotype ) {
        DirectObjectInjector.instance(  ConfigHelper.fnToSmallHumpName, stereotype ).inject(
                this.mjoInstanceConfig, stereotype, this
        );

        this.metier = Metier.queryMetier( this.mjoInstanceConfig.optString( "Metier" ) );
    }

    protected void reportScheme(){
        // TODO, Detailed scheme.
        this.infoLifecycle( String.format(
                "Standby => { Hierarchy => %s, Name => %s }", this.getHierarchy(), this.getInstanceFullName()
        ) );
    }

    @Override
    public Hierarchy getHierarchy() {
        return this.mHierarchy;
    }

    @Override
    public CascadeHeist parent() {
        return this.mParent;
    }

    @Override
    public RangedPage getMasterTaskPage() {
        return this.mMasterHeistTaskPage;
    }

    @Override
    public Heistium getHeistium() {
        return this.mHeistium;
    }

    @Override
    public HeistScheme getHeistScheme() {
        return this.heistScheme;
    }

    public String getIndexPath() {
        return this.indexPath;
    }


    @Override
    public JSONConfig getConfig() {
        return this.mjoInstanceConfig;
    }

    @Override
    public JSONConfig getProtoConfig() {
        return this.mjoProtoConfig;
    }

    @Override
    public ServgramOrchestrator getAttachedOrchestrator() {
        if( this.isRoot() ) {
            return this.getGramHeistletOrchestrator();
        }
        else {
            return this.parent().getThisHeistletOrchestrator();
        }
    }




    @Override
    public String heistName(){
        return this.mHeistName.rootName();
    }

    @Override
    public Namespace getHeistNamespace() {
        return this.mHeistName;
    }

    @Override
    public ChildHeistOrchestrator getThisHeistletOrchestrator() {
        return this.getHeistium().getHeistletOrchestrator();
    }

    @Override
    public HeistletOrchestrator getGramHeistletOrchestrator() {
        return this.getHeistgram().getHeistletOrchestrator();
    }

    @Override
    public void terminate(){
        this.mHeistium.terminate();
    }

    @Override
    public void toRavage() {
        this.infoLifecycle( Heistum.StatusStart );
        this.mHeistium.joinStartMultiTasks();
        //Debug.trace( "Deal!" );
    }

    @Override
    public void toStalk() {
        this.infoLifecycle( Heistum.StatusStart );
    }

    @Override
    public void toEmbezzle(){
        this.infoLifecycle( Heistum.StatusStart );
    }

    protected void executeSlaveMission() {
        this.infoLifecycle(  "It`s time to feast" );

        switch ( this.metier ) {
            case REAVER : {
                this.toRavage();
                break;
            }
            case STALKER : {
                this.toStalk();
                break;
            }
            case EMBEZZLER : {
                this.toEmbezzle();
                break;
            }
            default: {
                break;
            }
        }

        this.infoLifecycle( Heistum.StatusDone );
    }

    protected void executeMasterMission() throws ProxyProvokeHandleException {
        this.infoLifecycle( "orchestrating transactions" );

        try{
            this.getHeistium().getHeistletOrchestrator().orchestrate();
        }
        catch ( OrchestrateInterruptException e ) {
            throw new ProxyProvokeHandleException( e );
        }

        this.infoLifecycle( Heistum.StatusDone );
    }

    @Override
    public void toHeist() throws ProxyProvokeHandleException {
        if( this.getHierarchy() == Hierarchy.Slave ) {
            this.executeSlaveMission();
        }
        else {
            this.executeMasterMission();
        }
    }

}
