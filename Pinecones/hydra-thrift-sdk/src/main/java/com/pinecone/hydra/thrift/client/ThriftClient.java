package com.pinecone.hydra.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

public interface ThriftClient<T extends TServiceClient> {
    T getClient() throws TException;
    void close();
}
