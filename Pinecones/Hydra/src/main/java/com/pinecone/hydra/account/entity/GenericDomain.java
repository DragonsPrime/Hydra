package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;

import java.util.List;

public class GenericDomain extends ArchFolderElementNode  implements Domain{
    protected String domainName;

    public GenericDomain(){
        super();
    }

    public GenericDomain(AccountManager accountManager){
        super(accountManager);
    }

    @Override
    public String getDomainName() {
        return this.domainName;
    }

    @Override
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

}
