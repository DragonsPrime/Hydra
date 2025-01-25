package com.thrift;

import com.pinecone.hydra.thrift.client.GenericMultiplexedThriftClient;
import com.pinecone.hydra.thrift.client.GenericThriftClient;
import com.pinecone.hydra.thrift.server.GenericThriftServer;
import com.pinecone.hydra.thrift.server.MultiplexedServer;
import com.pinecone.hydra.thrift.server.ThriftServer;
import com.pinecone.hydra.thrift.service.HelloWorldService;
import com.pinecone.hydra.thrift.service.impl.HelloWorldServiceImpl;


public class TestThriftService {
    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(()->{
            HelloWorldService.Iface hello = new HelloWorldServiceImpl();
//            GenericThriftServer<HelloWorldService.Processor<HelloWorldService.Iface>> server = new GenericThriftServer<>(new HelloWorldService.Processor<>(hello), 8001);
//            server.start();
            MultiplexedServer multiplexedServer = new MultiplexedServer(8001);
            multiplexedServer.registerService( "Hello", new HelloWorldService.Processor<>(hello) );
            multiplexedServer.start();
        });
        thread.start();


        Thread.sleep( 1000 );

//        GenericThriftClient<HelloWorldService.Client> client = new GenericThriftClient<>("localhost", 8001, 30000, HelloWorldService.Client.class);
//        HelloWorldService.Client clientClient = client.getClient();
//        clientClient.sayHello("你好");
        GenericMultiplexedThriftClient thriftClient = new GenericMultiplexedThriftClient("localhost", 8001);
        HelloWorldService.Client hello = thriftClient.getClient("Hello", HelloWorldService.Client.class);
        hello.sayHello("你好");
    }
}
