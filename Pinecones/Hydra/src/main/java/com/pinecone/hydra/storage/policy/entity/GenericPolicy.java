package com.pinecone.hydra.storage.policy.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericPolicy implements Policy {
    protected long          enumId;
    protected String        policyName;
    protected GUID          policyGuid;
    protected String        policyDesc;

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public String getPolicyName() {
        return this.policyName;
    }

    @Override
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    @Override
    public GUID getPolicyGuid() {
        return this.policyGuid;
    }

    @Override
    public void setPolicyGuid(GUID policyGuid) {
        this.policyGuid = policyGuid;
    }

    @Override
    public String getPolicyDesc() {
        return this.policyDesc;
    }

    @Override
    public void setPolicyDesc(String policyDesc) {
        this.policyDesc = policyDesc;
    }
}
