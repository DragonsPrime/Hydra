package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;

public class GenericGroup extends ArchFolderElementNode implements Group{
    protected GUID defaultPrivilegePolicyGuid;

    public GenericGroup(){
        super();
    }

    public GenericGroup(AccountManager accountManager){
        super(accountManager);
    }
    @Override
    public GUID getDefaultPrivilegePolicyGuid() {
        return this.defaultPrivilegePolicyGuid;
    }

    @Override
    public void setDefaultPrivilegePolicyGuid(GUID defaultPrivilegePolicyGuid) {
        this.defaultPrivilegePolicyGuid = defaultPrivilegePolicyGuid;
    }


}
