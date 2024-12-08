package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericGroup implements Group{
    protected long enumId;

    protected GUID defaultPrivilegePolicyGuid;

    protected GUID guid;

    protected String name;

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public GUID getDefaultPrivilegePolicyGuid() {
        return this.defaultPrivilegePolicyGuid;
    }

    @Override
    public void setDefaultPrivilegePolicyGuid(GUID defaultPrivilegePolicyGuid) {
        this.defaultPrivilegePolicyGuid = defaultPrivilegePolicyGuid;
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
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
