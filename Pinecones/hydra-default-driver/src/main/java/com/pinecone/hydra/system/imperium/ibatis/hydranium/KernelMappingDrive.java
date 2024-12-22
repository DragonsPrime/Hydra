package com.pinecone.hydra.system.imperium.ibatis.hydranium;

import com.pinecone.hydra.entity.ibatis.hydranium.ArchMappingDriver;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMasterManipulatorImpl;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;

public class KernelMappingDrive extends ArchMappingDriver implements KOIMappingDriver {
    protected KOIMasterManipulator mKOIMasterManipulator;

    public KernelMappingDrive( Hydrarum system ) {
        super( system );
    }

    // Temp , TODO
    public KernelMappingDrive(Hydrarum system, IbatisClient ibatisClient, ResourceDispenserCenter dispenserCenter ) {
        super( system, ibatisClient, dispenserCenter, KernelMappingDrive.class.getPackageName().replace( "hydranium", "" ) );

        this.mKOIMasterManipulator = new RegistryMasterManipulatorImpl( this );
    }

    @Override
    public KOIMasterManipulator getMasterManipulator() {
        return this.mKOIMasterManipulator;
    }
}
