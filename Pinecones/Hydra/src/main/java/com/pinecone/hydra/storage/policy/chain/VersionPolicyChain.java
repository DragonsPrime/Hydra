package com.pinecone.hydra.storage.policy.chain;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.PolicyManage;
import com.pinecone.hydra.storage.version.source.VersionManipulator;

public class VersionPolicyChain implements PolicyChain {
    protected PolicyManage          policyManage;

    protected VersionManipulator    versionManipulator;

    public VersionPolicyChain( PolicyManage policyManage ){
        this.policyManage = policyManage;
    }
    @Override
    public GUID execution( String filePath, String version ) {
        return this.versionManipulator.queryObjectGuid(version, filePath);
    }
}
