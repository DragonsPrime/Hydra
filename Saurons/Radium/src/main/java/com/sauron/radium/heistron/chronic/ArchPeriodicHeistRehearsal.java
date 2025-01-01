package com.sauron.radium.heistron.chronic;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.RuntimeSystem;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.AnnotatedObjectInjector;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.framework.util.lang.NamespaceCollector;
import com.pinecone.framework.util.lang.PackageNameFetcher;
import com.pinecone.framework.util.name.FixScopeName;
import com.pinecone.framework.util.name.Name;
import com.pinecone.hydra.auto.PeriodicAutomaton;
import com.pinecone.hydra.auto.PeriodicAutomatron;
import com.sauron.radium.heistron.HTTPHeist;
import com.sauron.radium.heistron.Heistgram;
import com.sauron.radium.heistron.Heists;
import com.sauron.radium.heistron.Heistum;
import com.sauron.radium.heistron.orchestration.LocalHeistumOrchestrator;
import com.sauron.radium.system.RadiumSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ArchPeriodicHeistRehearsal implements PeriodicHeistRehearsal {
    protected Heistum             mHeistum;

    @JSONGet( "ChronicPerAcc" )
    protected long                mnChronicPerAcc;

    @JSONGet( "ChronicPeriods" )
    protected List<String >       mChronicPeriods;

    @JSONGet( "RaiderMarshaling" )
    protected JSONObject          mRaiderMarshaling;

    @JSONGet( "RaiderMarshaling.RaiderScopes" )
    protected List<String >       mRaiderScopes;

    @JSONGet( "RaiderMarshaling.MarshalingList" )
    protected List<String >       mMarshalingList;

    @JSONGet( "RaiderMarshaling.Raiders" )
    protected JSONObject          mRaiderConfigs;

    @JSONGet( "RaiderMarshaling.UsingSedation" )
    protected boolean             mbUsingSedation;

    protected List<Raider >       mExclusiveRaiders;
    protected List                mPreloadPrefixes;
    protected List                mPreloadSuffixes;
    protected RaiderFactory       mRaiderFactory;
    protected AtomicInteger       mIndexId;
    protected PeriodicAutomatron  mPrimaryAutomatron;

    protected ArchPeriodicHeistRehearsal( Heistum heistum , boolean bDaemon ) {
        this.mHeistum           = heistum;
        this.mIndexId           = new AtomicInteger();
        this.mExclusiveRaiders  = new ArrayList<>();
        RuntimeSystem system    = heistum.getSystem();
        if( system instanceof RadiumSystem ) {
            ( (RadiumSystem) system ).getPrimaryConfigScope().autoInject( ArchPeriodicHeistRehearsal.class, this.mHeistum.getConfig(), this );
        }
        else {
            AnnotatedObjectInjector injector = new AnnotatedObjectInjector( ArchPeriodicHeistRehearsal.class );
            injector.inject( this.mHeistum.getConfig(), this );
        }

        this.initDirectlyLoad();
        this.prepareFactory( new LocalRaiderFactory( this.mHeistum.getThisHeistletOrchestrator() ) );
        this.mPrimaryAutomatron = new PeriodicAutomaton(
                Heists.getCriterionNomenclatureName( this.mHeistum ), heistum.getHeistium(), this.mnChronicPerAcc, bDaemon
        );

        this.getAutomatron().command( new FeastInstructation( this ) );
    }


    @Override
    public AtomicInteger getIndexId() {
        return this.mIndexId;
    }

    @Override
    public List getPreloadPrefixes() {
        return this.mPreloadPrefixes;
    }

    @Override
    public List getPreloadSuffixes() {
        return this.mPreloadSuffixes;
    }

    @SuppressWarnings( "unchecked" )
    protected void initDirectlyLoad() {
        try{
            JSONObject jDirectlyLoad = this.mRaiderMarshaling.optJSONObject( LocalHeistumOrchestrator.ConfigDirectlyLoadKey );
            if( jDirectlyLoad == null ) {
                this.mPreloadPrefixes = new ArrayList<>();
                this.mPreloadSuffixes = new ArrayList<>();
            }
            else {
                this.mPreloadPrefixes = (List) jDirectlyLoad.getOrDefault( "Prefix", new ArrayList<>() );
                this.mPreloadSuffixes = (List) jDirectlyLoad.getOrDefault( "Suffix", new ArrayList<>() );
            }

            this.mPreloadPrefixes.add( "" );
            this.mPreloadSuffixes.add( "" );
        }
        catch ( Exception e ) {
            e.printStackTrace( this.getHeistgram().getSystem().console().getErr() );
        }
    }

    protected void prepareFactory( @Nullable RaiderFactory factory ) {
        if( factory != null ) {
            this.mRaiderFactory              = factory;
            String szCurrentPackageName      = this.getClass().getPackageName();
            this.mRaiderScopes.add( szCurrentPackageName );

            NamespaceCollector collector = new PackageNameFetcher( factory.getClassLoader() );
            List<String > children       = collector.fetch( szCurrentPackageName );
            this.mRaiderScopes.addAll( children );

            for( String sz : this.mRaiderScopes ) {
                this.mRaiderFactory.getClassScope().addScope( sz );
            }
            this.mRaiderFactory.getTraitClassLoader().updateScope();
        }
    }

    @Override
    public Heistgram getHeistgram() {
        return this.getParentHeist().getHeistgram();
    }

    @Override
    public Heistum getParentHeist() {
        return this.mHeistum;
    }

    @Override
    public PeriodicAutomatron getAutomatron() {
        return this.mPrimaryAutomatron;
    }

    @Override
    public List<String > getRawChronicPeriods() {
        return this.mChronicPeriods;
    }

    @Override
    public JSONObject getRaiderMarshalingConf() {
        return this.mRaiderMarshaling;
    }

    @Override
    public JSONObject getRaiderConfigs() {
        return this.mRaiderConfigs;
    }

    protected int nextId(){
        return this.getIndexId().getAndIncrement();
    }


    @Override
    public void vitalize() {
        for( String szRaider : this.mMarshalingList ) {
            List<Raider > list = this.popping( szRaider );
            this.mExclusiveRaiders.addAll( list );
        }

        for( Raider raider : this.mExclusiveRaiders ) {
            this.getAutomatron().command( raider.getPrimeDirective() );
            //this.getAutomatron().command( KernelInstructation.DIE );
//            try{
//                raider.getPrimeDirective().execute();
//            }
//            catch ( Exception e ) {
//
//            }

        }
//        this.getAutomatron().command(new Instructation() {
//            @Override
//            public void execute() throws Exception {
//                Debug.sleep(30000);
//                Debug.trace("shit");
//            }
//        });
        if( this.mbUsingSedation ){
            this.getAutomatron().command( new SedationInstructation( this ) );
        }
        //this.getAutomatron().command( KernelInstructation.TERMINATE );

        this.getAutomatron().start();
    }

    @Override
    public void joinVitalize() throws InterruptedException {
        this.vitalize();
        this.getAutomatron().join();
    }

    @SuppressWarnings( "unchecked" )
    protected List<Raider > popping( String szName ) {
        List<String > prefixes = new ArrayList<>( this.mPreloadPrefixes );
        prefixes.add( szName + "." );

        return this.mRaiderFactory.popping(
                new FixScopeName(szName, prefixes, (List<String >)this.mPreloadSuffixes),
                (HTTPHeist)this.getParentHeist(), this.nextId(), this.mRaiderConfigs.optJSONObject( szName )
        );
    }

    protected List<Raider > popping( Name name ) {
        return this.mRaiderFactory.popping( name, this.getHeistgram() );
    }
}
