package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericDomain implements Domain{
    protected long          enumId;

    protected String        domainName;

    protected GUID          domainGuid;

    protected String        name;

    @Override
    public Long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public String getDomainName() {
        return this.domainName;
    }

    @Override
    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public GUID getDomainGuid() {
        return this.domainGuid;
    }

    @Override
    public void setDomainGuid(GUID domainGuid) {
        this.domainGuid = domainGuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GUID getGuid() {
        return this.domainGuid;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
