package com.pinecone.hydra.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

public interface MultiplexedThriftClient {
    <T extends TServiceClient> T getClient(String serviceName, Class<T> clientClass) throws TException;


}
