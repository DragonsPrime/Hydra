package com.pinecone.hydra.storage.policy;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.source.BucketMasterManipulator;
import com.pinecone.hydra.storage.policy.entity.Policy;
import com.pinecone.hydra.storage.policy.source.PolicyFileMappingManipulator;
import com.pinecone.hydra.storage.policy.source.PolicyManipulator;
import com.pinecone.hydra.storage.policy.source.PolicyMasterManipulator;
import com.pinecone.hydra.storage.policy.source.VersionManipulator;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.pinecone.ulf.util.id.impl.GenericGuidAllocator;

import java.util.List;

public class TitanPolicyManage implements PolicyManage{
    protected PolicyManipulator                 policyManipulator;

    protected Hydrarum                          hydrarum;

    protected PolicyMasterManipulator           masterManipulator;

    protected GuidAllocator                     guidAllocator;

    protected PolicyFileMappingManipulator      policyFileMappingManipulator;

    protected VersionManipulator                versionManipulator;


    public TitanPolicyManage(Hydrarum hydrarum, KOIMasterManipulator masterManipulator, String name ){
       this.hydrarum                      = hydrarum;
       this.masterManipulator             = (PolicyMasterManipulator) masterManipulator;
       this.guidAllocator                 = new GenericGuidAllocator();
       this.policyManipulator             = this.masterManipulator.getPolicyManipulator();
       this.policyFileMappingManipulator  = this.masterManipulator.getPolicyFileMappingManipulator();
       this.versionManipulator            = this.masterManipulator.getVersionManipulator();
    }

    @Override
    public void insertPolicy(Policy policy) {
        this.policyManipulator.insert( policy );
    }

    @Override
    public void removePolicy(GUID policyGuid) {
        this.policyManipulator.remove( policyGuid );
    }

    @Override
    public Policy queryPolicy(GUID policyGuid) {
        return this.policyManipulator.queryPolicy( policyGuid );
    }

    @Override
    public void insertFilePolicyMapping(GUID policyGuid, String filePath) {
        this.policyFileMappingManipulator.insert( policyGuid, filePath );
    }

    @Override
    public void removeFilePolicyMapping(GUID policyGuid, String filePath) {
        this.policyFileMappingManipulator.remove( policyGuid, filePath );
    }

    @Override
    public List<GUID> queryPolicyGuid(String pathPath) {
        return this.policyFileMappingManipulator.queryPolicy(pathPath);
    }

    @Override
    public PolicyMasterManipulator getMasterManipulator() {
        return this.masterManipulator;
    }
}
