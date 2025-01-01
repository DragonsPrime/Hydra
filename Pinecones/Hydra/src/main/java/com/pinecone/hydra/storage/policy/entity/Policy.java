package com.pinecone.hydra.storage.policy.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface Policy extends Pinenut {
    long getEnumId();
    void setEnumId(long enumId);

    String getPolicyName();
    void setPolicyName(String policyName);

    GUID getPolicyGuid();
    void setPolicyGuid(GUID policyGuid);

    String getPolicyDesc();
    void setPolicyDesc(String policyDesc);
}
