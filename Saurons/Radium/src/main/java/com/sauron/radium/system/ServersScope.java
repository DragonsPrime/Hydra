package com.sauron.radium.system;

import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.HyComponent;
import com.pinecone.framework.util.config.JSONConfig;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.DirectObjectInjector;
import com.pinecone.hydra.server.ArchServer;
import com.pinecone.hydra.server.ArchServersCenter;
import com.pinecone.hydra.server.Server;
import com.pinecone.hydra.system.Hydrarum;
import com.sauron.radium.util.ConfigHelper;

import java.io.IOException;
import java.util.Map;

public class ServersScope extends ArchServersCenter implements Saunut {
    protected DirectObjectInjector mServerInjector;

    public ServersScope( Namespace name, Hydrarum system, HyComponent parent ) {
        super( name, system, parent );

        this.mServerInjector = new DirectObjectInjector( ConfigHelper.fnToSmallHumpName, ArchServer.class ) ;
        this.fetchAll();
        this.reinterpret();
    }

    public ServersScope( Hydrarum system, HyComponent parent ) {
        this( null, system, parent );
    }

    public ServersScope( Hydrarum system ) {
        this( system, null );
    }


    @Override
    public RadiumSystem getSystem() {
        return ( RadiumSystem ) super.getSystem();
    }

    @Override
    protected void  loadConfig() {
        JSONConfig sys  = (JSONConfig) this.getSystem().getSystemConfig();
        Object jServers = sys.opt( "Servers" );
        if( jServers instanceof String ) {
            try {
                this.serversConfig = sys.fromFile( this.getSystem().getWorkingPath().resolve( (String) jServers ).toFile()  );
            }
            catch ( IOException e ) {
                this.getSystem().handleKillException( e );
            }
        }
        else {
            this.serversConfig = (JSONObject) jServers;
        }

        sys.put( "Servers", this.serversConfig );
    }

    protected void  reinterpret() {
        for ( Map.Entry<String,Object > kv: this.getNickNameMap().entrySet() ) {
            this.getSystem().getGlobalConfigScope().put( kv.getKey(), ( (BasicServer)kv.getValue() ).getLocalDomain() );
        }
    }

    @Override
    protected Server newServer( JSONObject prototype )  {
        return new BasicServer( this, prototype );
    }

    DirectObjectInjector getServerInjector() {
        return this.mServerInjector;
    }
}
