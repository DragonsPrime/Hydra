package com.walnuts.sparta.uofs.console.thrift;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.thrift.client.GenericMultiplexedThriftClient;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.springframework.stereotype.Component;

@Component
public class AccountClient implements Pinenut {
    AccountIface.Client accountClient;
    public AccountClient() throws TException {
        GenericMultiplexedThriftClient thriftClient = new GenericMultiplexedThriftClient("localhost", 8001);
        accountClient = thriftClient.getClient("Account", AccountIface.Client.class);
    }

    public String queryNodeByPath( String path ) throws TException {
        return this.accountClient.queryNodeByPath( path );
    }
}
