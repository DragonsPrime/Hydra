package com.walnuts.sparta.account.rpc.thrift;

import com.pinecone.framework.util.json.JSONMaptron;
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
        MultiplexedServer multiplexedServer = new MultiplexedServer(
                new JSONMaptron("{host: \"0.0.0.0\",\n" +
                "port: 16701, SocketTimeout: 800, KeepAliveTimeout: 3600, MaximumConnections: 1e6}")
        );
        multiplexedServer.registerProcessor( "Account", new AccountIface.Processor<>(accountIfaceImpl) );
        multiplexedServer.start();
    }
}
