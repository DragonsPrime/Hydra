package com.walnuts.sparta.uofs.console.rpc.thrift;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.thrift.client.GenericMultiplexedThriftClient;
import com.walnuts.sparta.account.rpc.thrift.AccountIface;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

@Component
public class AccountClient implements Pinenut {
    AccountIface.Client accountClient;

    public AccountClient() throws TException {
//        GenericMultiplexedThriftClient thriftClient = new GenericMultiplexedThriftClient("localhost", 8081);
//        accountClient = thriftClient.getClient("Account", AccountIface.Client.class);
    }

    public String queryNodeByPath( String path ) throws TException {
        return null;
        //return this.accountClient.queryNodeByPath( path );
    }
}
