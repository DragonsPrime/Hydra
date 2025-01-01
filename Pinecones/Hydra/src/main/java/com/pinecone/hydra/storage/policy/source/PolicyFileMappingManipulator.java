package com.pinecone.hydra.storage.policy.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.entity.Policy;

import java.util.List;

public interface PolicyFileMappingManipulator extends Pinenut {
    void insert(GUID policyGuid, String filePath);

    void remove(GUID policyGuid, String filePath);

    List<GUID> queryPolicy(String filePath );
}
