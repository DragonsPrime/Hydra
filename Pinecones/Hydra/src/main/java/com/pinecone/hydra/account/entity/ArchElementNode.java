package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.pinecone.ulf.util.id.impl.GenericGuidAllocator;

public class ArchElementNode implements ElementNode{
    protected long              enumId;

    protected String            name;

    protected GUID              guid;

    protected AccountManager    accountManager;

    protected GuidAllocator     guidAllocator = new GenericGuidAllocator();

    public ArchElementNode(){
        this.guid = guidAllocator.nextGUID72();
    }

    public ArchElementNode(AccountManager accountManager){
        this.guid = guidAllocator.nextGUID72();
        this.accountManager = accountManager;
    }
    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
