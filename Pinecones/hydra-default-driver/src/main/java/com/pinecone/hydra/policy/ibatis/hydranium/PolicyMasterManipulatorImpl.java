package com.pinecone.hydra.policy.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.policy.ibatis.PolicyFileMappingMapper;
import com.pinecone.hydra.policy.ibatis.PolicyMapper;
import com.pinecone.hydra.storage.policy.source.PolicyFileMappingManipulator;
import com.pinecone.hydra.storage.policy.source.PolicyManipulator;
import com.pinecone.hydra.storage.policy.source.PolicyMasterManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;

import javax.annotation.Resource;
import java.util.Map;

public class PolicyMasterManipulatorImpl implements PolicyMasterManipulator {
    @Resource
    @Structure( type = PolicyMapper.class )
    PolicyManipulator policyMapping;

    @Resource
    @Structure( type = PolicyFileMappingMapper.class )
    PolicyFileMappingMapper policyFileMappingMapper;

    public PolicyMasterManipulatorImpl() {

    }

    public PolicyMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( PolicyMasterManipulatorImpl.class, Map.of(), this );
    }
    @Override
    public PolicyManipulator getPolicyManipulator() {
        return this.policyMapping;
    }

    @Override
    public PolicyFileMappingManipulator getPolicyFileMappingManipulator() {
        return this.policyFileMappingMapper;
    }


    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return null;
    }
}
