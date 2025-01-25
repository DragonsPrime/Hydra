package com.walnuts.sparta.account.rpc.thrift;

import com.pinecone.hydra.thrift.server.MultiplexedServer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class AccountRPCService {
    @Resource
    private AccountIfaceImpl accountIfaceImpl;

    @PostConstruct
    public void init(){
        MultiplexedServer multiplexedServer = new MultiplexedServer(8081);
        multiplexedServer.registerService( "Account", new AccountIface.Processor<>(accountIfaceImpl) );
        multiplexedServer.start();
    }
}
