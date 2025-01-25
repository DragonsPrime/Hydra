package com.pinecone.hydra.thrift.server;

import java.util.Map;

public interface ThriftServer {
    void start();

    void close();

    ServerConnectArguments getConnectionArguments();

    ThriftServer apply( Map<String, Object> conf );
}
