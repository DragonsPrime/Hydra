package com.pinecone.hydra.thrift.client;

public interface ThriftClient<T> {
    T getClient();
    void close();
}
