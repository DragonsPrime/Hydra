package com.walnuts.sparta.account.thrift;

import com.pinecone.hydra.thrift.server.MultiplexedServer;
import com.pinecone.hydra.thrift.service.HelloWorldService;
import com.walnuts.sparta.account.thrift.impl.AccountIfaceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ThriftServer {
    @Resource
    AccountIfaceImpl accountIfaceImpl;
    public ThriftServer(){
        MultiplexedServer multiplexedServer = new MultiplexedServer(8001);
        multiplexedServer.registerService( "Account", new AccountIface.Processor<>(accountIfaceImpl) );
        multiplexedServer.start();
    }
}
