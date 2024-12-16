package com.pinecone.hydra.storage.policy.chain;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.PolicyManage;
import com.pinecone.hydra.storage.policy.entity.Policy;
import com.pinecone.hydra.storage.policy.source.VersionManipulator;

public class VersionPolicyChain implements PolicyChain {
    protected PolicyManage          policyManage;

    protected VersionManipulator    versionManipulator;

    public VersionPolicyChain( PolicyManage policyManage ){
        this.policyManage = policyManage;
        this.versionManipulator = policyManage.getMasterManipulator().getVersionManipulator();
    }
    @Override
    public GUID execution( String filePath, String version ) {
        return this.versionManipulator.queryObjectGuid(version, filePath);
    }
}
