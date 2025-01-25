package com.walnuts.sparta.account.thrift.impl;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;
import com.walnuts.sparta.account.api.response.BasicResultResponse;
import org.apache.thrift.TException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AccountIfaceImpl implements AccountIface.Iface{
    @Resource
    private AccountManager primaryAccount;
    @Override
    public String queryNodeByPath(String path) throws TException {
        GUID guid = this.primaryAccount.queryGUIDByPath(path);
        return BasicResultResponse.success(this.primaryAccount.get(guid)).toJSONString();
    }
}
