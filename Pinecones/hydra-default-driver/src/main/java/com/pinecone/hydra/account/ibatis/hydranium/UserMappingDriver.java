package com.pinecone.hydra.account.ibatis.hydranium;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.entity.ibatis.hydranium.ArchMappingDriver;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.component.ResourceDispenserCenter;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.guid.GUIDs;

public class UserMappingDriver extends ArchMappingDriver implements KOIMappingDriver {
    protected KOIMasterManipulator mKOIMasterManipulator;

    public UserMappingDriver( Processum superiorProcess ) {
        super(superiorProcess);
    }

    public UserMappingDriver( Processum superiorProcess, IbatisClient ibatisClient, ResourceDispenserCenter dispenserCenter ) {
        super( superiorProcess, ibatisClient, dispenserCenter, UserMappingDriver.class.getPackageName().replace( "hydranium", "" ) );

        this.mKOIMasterManipulator = new UserMasterManipulatorImpl( this );
    }

    @Override
    public KOIMasterManipulator getMasterManipulator() {
        return this.mKOIMasterManipulator;
    }
}
