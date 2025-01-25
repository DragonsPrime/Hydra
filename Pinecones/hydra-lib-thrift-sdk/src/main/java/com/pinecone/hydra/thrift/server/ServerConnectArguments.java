package com.pinecone.hydra.thrift.server;

import com.pinecone.hydra.thrift.MCConnectionArguments;

public interface ServerConnectArguments extends MCConnectionArguments {
    int getMaximumClients() ;

    void setMaximumClients( int mnMaximumClients );
}
