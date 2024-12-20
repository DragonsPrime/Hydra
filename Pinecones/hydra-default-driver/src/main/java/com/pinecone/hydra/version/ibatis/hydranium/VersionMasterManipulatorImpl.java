package com.pinecone.hydra.version.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.policy.ibatis.PolicyFileMappingMapper;
import com.pinecone.hydra.policy.ibatis.hydranium.PolicyMasterManipulatorImpl;
import com.pinecone.hydra.storage.version.source.VersionManipulator;
import com.pinecone.hydra.storage.version.source.VersionMasterManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.version.ibatis.VersionMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class VersionMasterManipulatorImpl implements VersionMasterManipulator {
    @Resource
    @Structure( type = VersionMapper.class )
    VersionManipulator versionManipulator;

    public VersionMasterManipulatorImpl() {

    }

    public VersionMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( VersionMasterManipulatorImpl.class, Map.of(), this );
    }

    @Override
    public VersionManipulator getVersionManipulator() {
        return this.versionManipulator;
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return null;
    }
}
