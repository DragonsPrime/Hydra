package com.pinecone.hydra.file.ibatis.hydranium;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.entity.ibatis.hydranium.ArchMappingDriver;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMappingDriver;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMasterManipulatorImpl;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;

public class FileMappingDriver extends ArchMappingDriver implements KOIMappingDriver {
    protected KOIMasterManipulator mKOIMasterManipulator;

    public FileMappingDriver( Processum superiorProcess  ) {
        super( superiorProcess );
    }

    // Temp , TODO
    public FileMappingDriver( Processum superiorProcess, IbatisClient ibatisClient, ResourceDispenserCenter dispenserCenter ) {
        super( superiorProcess, ibatisClient, dispenserCenter, FileMappingDriver.class.getPackageName().replace( "hydranium", "" ) );

        this.mKOIMasterManipulator = new FileMasterManipulatorImpl( this );
    }

    @Override
    public KOIMasterManipulator getMasterManipulator() {
        return this.mKOIMasterManipulator;
    }
}
