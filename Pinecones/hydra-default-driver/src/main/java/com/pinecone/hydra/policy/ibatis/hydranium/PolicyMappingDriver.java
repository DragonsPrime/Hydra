package com.pinecone.hydra.policy.ibatis.hydranium;

import com.pinecone.hydra.bucket.ibatis.hydranium.BucketMappingDriver;
import com.pinecone.hydra.bucket.ibatis.hydranium.BucketMasterManipulatorImpl;
import com.pinecone.hydra.entity.ibatis.hydranium.ArchMappingDriver;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;

public class PolicyMappingDriver extends ArchMappingDriver implements KOIMappingDriver {
    protected KOIMasterManipulator mKOIMasterManipulator;

    public PolicyMappingDriver( Hydrarum system ) {
        super( system );
    }

    // Temp , TODO
    public PolicyMappingDriver(Hydrarum system, IbatisClient ibatisClient, ResourceDispenserCenter dispenserCenter ) {
        super( system, ibatisClient, dispenserCenter, PolicyMappingDriver.class.getPackageName().replace( "hydranium", "" ) );

        this.mKOIMasterManipulator = new PolicyMasterManipulatorImpl( this );
    }

    @Override
    public KOIMasterManipulator getMasterManipulator() {
        return this.mKOIMasterManipulator;
    }
}
