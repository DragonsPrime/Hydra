package com.pinecone.hydra.thrift.server;

import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.thrift.SharedConnectionArguments;

public class ServerConnectionArguments extends SharedConnectionArguments implements ServerConnectArguments {
    protected int mnMaximumClients; // 0 <= for unlimited clients

    public ServerConnectionArguments( JSONObject args ) {
        super( args );
        this.mnMaximumClients  = args.optInt( "MaximumClients", 0 );
    }

    @Override
    public int getMaximumClients() {
        return this.mnMaximumClients;
    }

    @Override
    public void setMaximumClients( int mnMaximumClients ) {
        this.mnMaximumClients = mnMaximumClients;
    }
}
