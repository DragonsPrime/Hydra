package com.pinecone.hydra.thrift;

import com.pinecone.framework.util.json.JSONObject;

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
