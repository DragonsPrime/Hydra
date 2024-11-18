package com.sauron.radium;

import com.pinecone.hydra.Hydra;
import com.pinecone.hydra.Hydradom;
import com.pinecone.hydra.servgram.ServgramOrchestrator;
import com.pinecone.hydra.system.component.GenericResourceDispenserCenter;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.component.GenericTracerScope;
import com.pinecone.hydra.system.component.Slf4jTraceable;
import com.pinecone.hydra.system.component.Slf4jTracerScope;
import com.pinecone.hydra.system.component.LogStatuses;
import com.sauron.radium.system.*;
import com.pinecone.framework.unit.MultiScopeMap;
import com.pinecone.framework.util.config.JSONSystemConfig;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.kanina.RabbitMQClient;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.io.Tracerson;
import com.pinecone.framework.util.json.homotype.DirectObjectInjector;
import com.pinecone.Pinecone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 *  Bean Nuts Hazelnut Sauron Radium
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Radium Spider for PB-Level Automatic Crawler
 *
 *  *****************************************************************************************
 *  DragonKing.me of Harold
 */
public class Radium extends Hydradom implements RadiumSystem, Slf4jTraceable {
    public static final String  NUTLET_NAME         = "Bean Nuts Hazelnut Sauron Nonabyte Radium";
    public static final long    VER_PINE            =  202506L;
    public static final String  VERSION             = "2.1.0";
    public static final String  RELEASE_DATE        = "2025/06/06";
    public static final String  ROOT_SERVER         = "http://www.rednest.cn/";
    public static final String  CONTACT_INFO        = "E-Mail:arb#rednest.cn"; // Giving your contact information, if this program interrupt abnormally.

    public static final String  SYSTEM_PATH         = "./system/";
    public static final String  SETUP_PATH          = Radium.SYSTEM_PATH + "setup/";
    public static final String  MAIN_CONFIG_FILE    = Radium.SETUP_PATH + "config.json5";

    protected String                               mMinionName;

    protected Hierarchy                            mServiceHierarchy = Hierarchy.H_SLAVE;
    protected boolean                              mMasterQuery;
    protected Logger                               mLogger;

    protected Path                                 mPrimaryConfigPath        ;


    protected DirectObjectInjector                 mObjectInjector           ;
    protected ServersScope                         mServersScope             ;
    protected StorageSystem                        mStorageSystem            ;
    protected Slf4jTracerScope                     mTracerScope              ;
    protected SystemDaemon                         mSystemPrimaryDaemon      ;
    protected ConfigScope                          mPrimaryConfigScope       ; // Program runtime global variable retrieving config-scope.
    protected MiddlewareManager                    mMiddlewareManager        ;
    protected ResourceDispenserCenter              mDispenserCenter          ;

    protected void prepare_system_log4j_logger() {
        this.mLogger = LoggerFactory.getLogger( this.className() + "<PrimarySystem>" );
        this.pout().print( "[System] [TracerReassignment] <Transfer console -> Slf4j>\n" );
    }

    private void load_this_class_config() {
        this.mjoGlobalConfig.addParentPath( this.getWorkingPath() );
        this.mjoSystemConfig     = this.mjoGlobalConfig.getChild( "System" );

        this.mObjectInjector     = DirectObjectInjector.instance(  true, Radium.class );
        this.mObjectInjector.inject( this.mjoSystemConfig, Radium.class, this );
        this.mObjectInjector.inject( this.mjoSystemConfig, Hydra.class, this );

        this.mServiceHierarchy = Hierarchy.queryHierarchy( this.mjoSystemConfig.optString( "ServiceArch" ) );
    }

    protected void prepare_system_skeleton() {
        this.infoLifecycle( "Skeleton Initialization", LogStatuses.StatusStart );
        this.mTracerScope            = new GenericTracerScope( this );
        this.mPrimaryConfigScope     = new ConfigScope( ConfigScope.KeyGlobal, this, this.getGlobalConfig() );
        this.mMiddlewareManager      = new MiddlewareManager( this );
        this.mServersScope           = new ServersScope( this );
        this.mStorageSystem          = new StorageSystem( this );
        this.mSystemPrimaryDaemon    = new SystemDaemon( this );
        this.mDispenserCenter        = new GenericResourceDispenserCenter( this );


        this.getComponentManager().addComponent( this.mMiddlewareManager    );
        this.getComponentManager().addComponent( this.mPrimaryConfigScope   );
        this.getComponentManager().addComponent( this.mServersScope         );
        this.getComponentManager().addComponent( this.mStorageSystem        );
        this.getComponentManager().addComponent( this.mTracerScope          );
        this.getComponentManager().addComponent( this.mSystemPrimaryDaemon  );
        this.getComponentManager().addComponent( this.mDispenserCenter      );
        //Debug.trace( this.getComponentManager().getComponents() );
        //Debug.echo( ( (JSONObject)this.getGlobalConfigScope().thisScope() ).toJSONStringI(4) );

        super.prepare_system_skeleton();
        this.infoLifecycle( "Skeleton Initialization", LogStatuses.StatusReady );
    }

    @Override
    protected void loadConfig() {
        try {
            Map<String, String[] > map = this.getStartupCommandMap();
            String[] args = map.get( "workingPath" );
            if( args != null && args.length > 0 ) {
                this.mWorkingPath =  Path.of( args[ 0 ] );
            }
            else {
                this.mWorkingPath      = Path.of( this.getRuntimeContextPath() );
            }

            args = map.get( "config" );
            if( args != null && args.length > 0 ) {
                this.mPrimaryConfigPath = Path.of( args[ 0 ] );
            }
            else {
                this.mPrimaryConfigPath = this.getWorkingPath().resolve( Radium.MAIN_CONFIG_FILE );
            }
            this.mjoGlobalConfig   = (JSONSystemConfig) ( new JSONSystemConfig( this ) ).apply( this.mPrimaryConfigPath.toFile() );
        }
        catch ( IOException e ) {
            this.handleKillException( e );
        }
    }


    protected void loadTracer() {
        this.mConsole = new Tracerson();
    }

    @Override
    protected void onlyLoadTaskManager() {
        this.mTaskManager = new MasterServgramOrchestrator( this );
    }

    protected void traceSubsystemWelcomeInfo() {
        this.pout().print( "---------------------------------------------------------------\n" );
    }

    @Override
    protected void traceWelcomeInfo() {
        this.pout().print( "---------------------------------------------------------------\n" );
        this.pout().print( "\u001B[31m\uD83D\uDE08 Sauron Eyes Radium Framework \uD83D\uDE08 \u001B[0m\n" );
        this.pout().print( "\u001B[34mBean Sauron Nonabyte, Project.`the Grand Design` \u001B[0m\n" );
        this.pout().print( "\u001B[32mBean Nuts Digital Datacenter of Cthulhu Databases \u001B[0m\n" );
        this.pout().print( "\u001B[32mCopyright(C) 2008-2028 Bean Nuts Foundation. All rights reserved.\u001B[0m\n" );
        this.pout().print( "---------------------------------------------------------------\n" );
        this.pout().print( "Greeting! My name is Sauron, I insight omniscience.\n" );
        this.pout().print( "Salve! Nomen Sauron est, omnia perspicio.\n" );
        this.pout().print( "----------------------Kernel Information-----------------------\n" );
        this.pout().print( "PineconeVer  : Bean Nuts Pinecone Ursus " + Pinecone.VERSION + "\n" );
        this.pout().print( "PineconeAlly : Bean Nuts Acorn Hydra\n" );
        this.pout().print( "PineconeAlly : Bean Nuts Walnut Ulfhedinn (Pinecone Framework Edition)\n"   );
        this.pout().print( "PineconeAlly : Bean Nuts Walnut Sparta\n" );
        this.pout().print( "PineconeAlly : Bean Nuts Acorn Summer (Pinecone Framework Edition)\n" );
        this.pout().print( "PineconeAlly : Bean Nuts Hazelnut Slime\n"   );
        this.pout().print( "PineconeAlly : Bean Nuts Hazelnut Sauron Radium\n"   );
        this.pout().print( "PineconeAlly : Bean Nuts Acorn Summer Springron (org.springframework.boot 2.4.1)\n"   );

        this.traceSubsystemWelcomeInfo();
        this.prepare_system_log4j_logger();
        this.infoLifecycle( "Initialization", LogStatuses.StatusStart );
    }

    protected void traceSystemInfo() {
        LocalDateTime now = LocalDateTime.now();
        this.console().echo( "----------------------System Information-----------------------\n" );
        this.console().echo( "MinionName : " + this.mMinionName, "\n"   );
        this.console().echo( "NutletName : " + Radium.NUTLET_NAME , "\n"  );
        this.console().echo( "Version    : " + Radium.VERSION, "\n"        );
        this.console().echo( "ReleaseDate: " + Radium.RELEASE_DATE, "\n"   );
        this.console().echo( "ServiceID  : " + this.mServiceID, "\n"   );
        this.console().echo( "ServiceArch: " + this.mServiceHierarchy.getName(), "\n" );
        this.console().echo( "RuntimePath: " + this.getRuntimePath(), "\n" );
        this.console().echo( "ContextPath: " + this.getRuntimeContextPath(), "\n" );
        this.console().echo( "PrimaryConf: " + this.mPrimaryConfigPath.toString(), "\n" );
        this.console().echo( "StartTime  : " + now.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS") ), "\n"   );
        this.console().echo( "---------------------------------------------------------------\n" );
    }


    public Radium( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Radium( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );

        this.load_this_class_config();
        this.prepare_system_skeleton();
        this.loadTracer();
        this.dispatchStartupCommand();

        this.traceSystemInfo();
        this.infoLifecycle( "Initialization", LogStatuses.StatusReady );
        this.console().echo( "\n\n" );
        this.console().getOut().flush();
    }


    @Override
    public Logger getLogger() {
        return this.mLogger;
    }

    @Override
    public Radium infoLifecycle( String szWhat, String szStateOrExtra ) {
        this.getLogger().info( "[SystemLifecycle] [{}] <{}>", szWhat, szStateOrExtra );
        return this;
    }

    @Override
    public Hierarchy getServiceArch() {
        return this.mServiceHierarchy;
    }

    @Override
    public boolean isTopmostArchy() {
        return this.getServiceArch() == this.getTopmostArchy();
    }

    @Override
    public Hierarchy getTopmostArchy() {
        return Hierarchy.H_MASTER;
    }

    @Override
    public Hierarchy getBottommostArchy() {
        return Hierarchy.H_SLAVE;
    }

    @Override
    public boolean isBottommostArchy() {
        return this.getServiceArch() == this.getBottommostArchy();
    }

    @Override
    public SystemDaemon getSystemDaemon() {
        return this.mSystemPrimaryDaemon;
    }

    @Override
    public ServersScope getServersScope() {
        return this.mServersScope;
    }

    @Override
    public StorageSystem getStorageSystem() {
        return this.mStorageSystem;
    }

    @Override
    public Slf4jTracerScope getTracerScope() {
        return this.mTracerScope;
    }

    @Override
    public ResourceDispenserCenter getDispenserCenter() {
        return this.mDispenserCenter;
    }

    @Override
    public MultiScopeMap<String, Object> getGlobalConfigScope() {
        return this.getPrimaryConfigScope().getScopeMap();
    }

    @Override
    public ConfigScope getPrimaryConfigScope() {
        return this.mPrimaryConfigScope;
    }

    public boolean getMasterQuery() {
        return this.mMasterQuery;
    }

    public boolean isKingMasterQuery() {
        return this.getMasterQuery() && (this.isTopmostArchy() || this.getServiceArch() == Hierarchy.H_PALADIN);
    }

    @Override
    public MiddlewareManager getMiddlewareManager() {
        return this.mMiddlewareManager;
    }

    public ServgramOrchestrator getServgramOrchestrator() {
        return (ServgramOrchestrator) this.mTaskManager;
    }

    public Path getPrimaryConfigsPath() {
        return this.getWorkingPath().resolve( Radium.SETUP_PATH );
    }

    public void vitalize () throws Exception {
        this.getServgramOrchestrator().tracer().info( "[Lifecycle] <System committed prime directive>" );
        this.getServgramOrchestrator().orchestrate();


        //( new Heistron( "Heist", this )).execute();



//        LocalHeistium heistium = new LocalHeistium( "Test", this, 5, null );
//        heistium.joinStartMultiTasks();

//        RangedPage64 page64   = new RangedPage64( 0, 1000,0 );
//        DirectPagePool pagePool = new DirectPagePool( LocalTaskPage.class );
//
//        LocalMultiActiveTaskPageProducer producer = new LocalMultiActiveTaskPageProducer( new FixedPageDivider64( page64, pagePool, 100 ), page64.getId() + 1 );
//        LocalSingleTaskPageConsumer consumer = new LocalSingleTaskPageConsumer( producer );
//
//        consumer.consume();
    }


    void testBunny() throws Exception {
        RabbitMQClient bunny = new RabbitMQClient( this, this.getMiddlewareManager().getMiddlewareConfig().optJSONObject( "Messengers" ).optJSONObject( "RabbitMQKingpin" ) );
        bunny.toListen();

        Debug.echo( bunny );
    }

}
