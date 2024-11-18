package com.sauron.radium.ally.rdb;

import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.ArchSystemAutoAssembleComponent;
import com.pinecone.hydra.system.HyComponent;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.slime.source.rdb.RDBClient;
import com.sauron.radium.system.MiddlewareManager;
import com.sauron.radium.system.RadiumSystem;
import com.sauron.radium.system.Saunut;

import java.util.Collection;
import java.util.Map;

public class RDBManager extends ArchSystemAutoAssembleComponent implements Saunut, HyComponent {
    @JSONGet( "RDBs" )
    protected JSONObject                           mjoRDBsConf             ;

    @JSONGet( "RDBs.Configs" )
    protected JSONObject                           mjoComponentConf        ;

    @JSONGet( "RDBs.Databases" )
    protected JSONObject                           mjoDatabases            ;

    @JSONGet( "RDBs.Configs.JDBC.Driver" )
    protected String                               mszJDBCDriverName       ;

    @JSONGet( "RDBs.Configs.Ibatis.Client" )
    protected String                               mszIBatisClient         ;

    @JSONGet( "RDBs.Configs.Enable" )
    protected boolean                              mbEnable                ;

    protected Map<String, RDBClient >              mRDBClientComponent     ;


    public RDBManager( Namespace name, HyComponent parent ) {
        super( name, parent.getSystem(), parent.getSystem().getComponentManager(), parent );
        Hydrarum system = parent.getSystem();

        MiddlewareManager parentManager = (MiddlewareManager) parent;

        this.getSystem().getPrimaryConfigScope().autoInject( RDBManager.class, parentManager.getMiddlewareConfig() , this );

        this.mRDBClientComponent = new LinkedTreeMap<>();
        this.prepareInstanceClient();

        this.infoLifecycleInitializationDone();
    }

    public RDBManager( HyComponent parent ) {
        this( null, parent );
    }

    protected void prepareInstanceClient() {
        for( Object o : this.mjoDatabases.entrySet() ) {
            Map.Entry kv   = (Map.Entry) o;

            JSONObject val = (JSONObject) kv.getValue();
            this.mObjectOverrider.override( val, this.mjoComponentConf, false );

            try{
                String szEngine = val.optString( "Engine" );
                String szInsNam = (String) kv.getKey();

                boolean bEnable = val.optBoolean( "Enable" );
                if( bEnable ) {
                    Object client = this.mUniformFactory.loadInstance( szEngine, null, new Object[] { this, szInsNam } );
                    if( client instanceof RDBClient ){
                        this.mRDBClientComponent.put( szInsNam, (RDBClient)client );
                    }
                    else {
                        throw new IllegalArgumentException( "Illegal client engine, should be `RDBClient`: " + szEngine );
                    }
                }
            }
            catch ( Exception e ) {
                throw new ProvokeHandleException( e );
            }
        }

        //Debug.fmt( 2, this.mjoDatabases );
    }

    @Override
    public RadiumSystem getSystem() {
        return ( RadiumSystem ) super.getSystem();
    }

    public String getJDBCDriverName() {
        return this.mszJDBCDriverName;
    }

    public JSONObject getComponentConf() {
        return this.mjoComponentConf;
    }

    @Override
    public DynamicFactory getSharedUniformFactory() {
        return this.mUniformFactory;
    }

    public boolean isEnable() {
        return this.mbEnable;
    }

    public JSONObject getDatabases() {
        return this.mjoDatabases;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<String > databasesNames() {
        return (Collection)this.getDatabases().values();
    }

    public RDBClient getRDBClientByName ( String szName ) {
        return this.mRDBClientComponent.get( szName );
    }

    public RDBClient terminate( String szName ) {
        RDBClient client = this.getRDBClientByName( szName );
        if( client != null ) {
            client.close();
            if( client.isTerminated() ) {
                this.mRDBClientComponent.remove( szName );
            }
            else {
                return null;
            }
        }
        return client;
    }

    public int clientSize() {
        return this.mRDBClientComponent.size();
    }
}
