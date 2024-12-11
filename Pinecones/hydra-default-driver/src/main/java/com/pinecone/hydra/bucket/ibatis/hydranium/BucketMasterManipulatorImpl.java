package com.pinecone.hydra.bucket.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.bucket.ibatis.BucketMapping;
import com.pinecone.hydra.storage.bucket.source.BucketManipulator;
import com.pinecone.hydra.storage.bucket.source.BucketMasterManipulator;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMasterManipulatorImpl;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class BucketMasterManipulatorImpl implements BucketMasterManipulator {
    @Resource
    @Structure( type = BucketMapping.class )
    BucketManipulator bucketMapping;

    public BucketMasterManipulatorImpl() {

    }

    public BucketMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( BucketMasterManipulatorImpl.class, Map.of(), this );
    }
    @Override
    public BucketManipulator getBucketManipulator() {
        return this.bucketMapping;
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return null;
    }
}
