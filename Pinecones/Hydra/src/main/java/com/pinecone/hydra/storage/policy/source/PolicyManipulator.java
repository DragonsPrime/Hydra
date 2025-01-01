package com.pinecone.hydra.storage.policy.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.entity.Policy;

public interface PolicyManipulator extends Pinenut {
    void insert(Policy policy);

    void remove(GUID policyGuid);

    Policy queryPolicy( GUID policyGuid );
}
