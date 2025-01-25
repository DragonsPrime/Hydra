package com.thrift;

import com.pinecone.hydra.thrift.client.GenericThriftClient;
import com.pinecone.hydra.thrift.service.HelloWorldService;
import org.apache.thrift.TException;

public class TestThriftClient {
    public static void main(String[] args) throws TException {
        GenericThriftClient<HelloWorldService.Client> client = new GenericThriftClient<>("localhost", 8001, 30000, HelloWorldService.Client.class);
        HelloWorldService.Client clientClient = client.getClient();
        clientClient.sayHello("你好");
    }
}
