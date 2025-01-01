package com.sauron.shadow.chronicle;

import com.pinecone.framework.system.NonNull;
import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.ProxyProvokeHandleException;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.framework.util.config.JSONConfig;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.slime.jelly.source.ibatis.SoloSessionMapperPool;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.sauron.radium.heistron.CascadeHeist;
import com.sauron.radium.heistron.Crew;
import com.sauron.radium.heistron.HTTPHeist;
import com.sauron.radium.heistron.Heistgram;
import com.sauron.radium.heistron.chronic.PeriodicHeistRehearsal;
import com.sauron.radium.heistron.orchestration.Heistlet;
import com.sauron.shadow.chronicle.dao.BasicChronicleManipulator;
import org.apache.ibatis.session.SqlSession;

import java.util.Map;


//@Heistlet( "Chronicle" )
public class ChronicleHeist extends HTTPHeist implements Chronicle {
    protected PeriodicHeistRehearsal       mPeriodicHeistKernel;

    @JSONGet( "PrimaryRDB" )
    protected String                       mszPrimaryRDBName   ;

    protected IbatisClient                 mPrimaryDataIbatisClient ;

    protected SqlSession                   mPrimarySharedSqlSession;

    @Structure
    protected BasicChronicleManipulator    mBasicChronicleManipulator;


    public ChronicleHeist( Heistgram heistron ){
        super( heistron );
        this.initSelf();
    }

    public ChronicleHeist( Heistgram heistron, JSONConfig joConfig ){
        super( heistron, joConfig );
        this.initSelf();
    }

    public ChronicleHeist( Heistgram heistron, @Nullable CascadeHeist parent, @NonNull String szChildName ) {
        super( heistron, parent, szChildName );
        this.initSelf();
    }

    protected void initSelf() {
        if( this.isSlave() ) {
            this.mPeriodicHeistKernel     = new ChroniclePeriodicHeistKernel( this );
            this.getSystem().getPrimaryConfigScope().autoInject( ChronicleHeist.class, this.getConfig(), this );
            this.mPrimaryDataIbatisClient = (IbatisClient) this.getSystem().getMiddlewareManager().getRDBManager().getRDBClientByName( this.mszPrimaryRDBName );
            this.mPrimarySharedSqlSession = this.mPrimaryDataIbatisClient.openSession( true );
            this.prepareChildrenConfig();
            this.prepareScopeDAOManipulator();
            this.getSystem().getPrimaryConfigScope().autoConstruct( ChronicleHeist.class, this.getConfig(), this );
        }
    }

    protected void prepareChildrenConfig() {
        JSONObject jo = this.mPeriodicHeistKernel.getRaiderConfigs();

        for( Object o : jo.entrySet() ) {
            Map.Entry kv  = (Map.Entry) o;

            JSONObject jc = (JSONObject)kv.getValue();
            jc.put( "__proto__", this.getConfig() ); // Jesus, no needs to override... Just using prototype chain.
        }
    }

    protected void prepareScopeDAOManipulator() {
        this.mPrimaryDataIbatisClient.addDataAccessObjectScope( this.getClass().getPackageName() );

        this.getSystem().getDispenserCenter().getInstanceDispenser().register(
                BasicChronicleManipulator.class,
                new SoloSessionMapperPool( this.mPrimarySharedSqlSession, BasicChronicleManipulator.class )
        );
        //this.mBasicChronicleManipulator = this.mPrimarySharedSqlSession.getMapper( BasicChronicleManipulator.class );
    }

    @Override
    public IbatisClient getPrimaryDataIbatisClient() {
        return this.mPrimaryDataIbatisClient;
    }

    @Override
    public SqlSession getPrimarySharedSqlSession() {
        return this.mPrimarySharedSqlSession;
    }

    @Override
    public BasicChronicleManipulator getBasicChronicleManipulator(){
        return this.mBasicChronicleManipulator;
//        return new BasicChronicleManipulator() {
//            @Override
//            public void insertOneNews(String szTableName, String szObjectName, String szDateTime, String szNewsIndex) {
//
//            }
//        };
    }

    @Override
    public Crew newCrew( int nCrewId ) {
        return new ChronicleReaver( this, nCrewId );
    }

    @Override
    public void toRavage(){
        ChronicleHeist.this.infoLifecycle( "Chronicle Heist Vitalization","Vitalized" );

//        ZhihuClerk clerk = new ZhihuClerk(
//                this, 1999, this.mPeriodicHeistKernel.getRaiderConfigs().optJSONObject( "Zhihu" )
//        );
//        clerk.toRavage();
//        clerk.toRavage();
//        clerk.toRavage();
        try{
            this.mPeriodicHeistKernel.joinVitalize();
        }
        catch ( InterruptedException e ) {
            throw new ProxyProvokeHandleException( e );
        }

        ChronicleHeist.this.infoLifecycle( "Chronicle Heist Termination","Terminated" );
    }

    @Override
    public void toStalk(){

    }
}
