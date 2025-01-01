package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.wolfmc.client.ArchAsyncMessenger;

public abstract class SharedConnectionArguments implements MCConnectionArguments {
    protected String         mszHost;
    protected short          mnPort;
    protected int            mnKeepAliveTimeout;
    protected int            mnSocketTimeout;


    public SharedConnectionArguments( JSONObject args ) {
        this.mszHost             = args.optString( "host", null );
        this.mnPort              = (short) args.optInt( "port", -1 );
        this.mnKeepAliveTimeout  = args.optInt( "KeepAliveTimeout" );
        this.mnSocketTimeout     = args.optInt( "SocketTimeout", 800 );
    }

    public SharedConnectionArguments( ArchAsyncMessenger args ) {
        this( args.getSectionConf() );
    }

    @Override
    public String getHost() {
        return this.mszHost;
    }

    @Override
    public void setHost( String host ) {
        this.mszHost = host;
    }

    @Override
    public short getPort() {
        return this.mnPort;
    }

    @Override
    public void setPort( short port ) {
        this.mnPort = port;
    }

    @Override
    public int getKeepAliveTimeout() {
        return this.mnKeepAliveTimeout;
    }

    @Override
    public void setKeepAliveTimeout( int keepAliveTimeout ) {
        this.mnKeepAliveTimeout = keepAliveTimeout;
    }

    @Override
    public int getSocketTimeout() {
        return this.mnSocketTimeout;
    }

    @Override
    public void setSocketTimeout( int socketTimeout ) {
        this.mnSocketTimeout = socketTimeout;
    }
}
