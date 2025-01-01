package com.pinecone.hydra.storage.policy;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.entity.Policy;
import com.pinecone.hydra.storage.policy.source.PolicyMasterManipulator;

import java.util.List;

public interface PolicyManage extends Pinenut {
    void insertPolicy( Policy policy );

    void removePolicy(GUID policyGuid );

    Policy queryPolicy( GUID policyGuid );

    void insertFilePolicyMapping( GUID policyGuid, String filePath );

    void removeFilePolicyMapping( GUID policyGuid, String filePath );

    List<GUID> queryPolicyGuid(String pathPath );

    PolicyMasterManipulator getMasterManipulator();
}
