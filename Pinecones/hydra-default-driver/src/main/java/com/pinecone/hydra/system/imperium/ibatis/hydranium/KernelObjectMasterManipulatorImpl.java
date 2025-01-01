package com.pinecone.hydra.system.imperium.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.system.imperium.ibatis.KernelObjectMapper;
import com.pinecone.hydra.system.imperium.ibatis.KernelObjectMetaMapper;
import com.pinecone.hydra.system.imperium.source.KernelObjectManipulator;
import com.pinecone.hydra.system.imperium.source.KernelObjectMasterManipulator;
import com.pinecone.hydra.system.imperium.source.KernelObjectMetaManipulator;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMasterManipulatorImpl;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class KernelObjectMasterManipulatorImpl implements KernelObjectMasterManipulator {
    @Resource
    @Structure( type = KernelObjectMapper.class )
    KernelObjectManipulator         kernelObjectManipulator;
    @Resource
    @Structure( type = KernelObjectMetaMapper.class )
    KernelObjectMetaManipulator     kernelObjectMetaManipulator;

    public KernelObjectMasterManipulatorImpl() {

    }

    public KernelObjectMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( RegistryMasterManipulatorImpl.class, Map.of(), this );
    }

    @Override
    public KernelObjectManipulator getKernelObjectManipulator() {
        return this.kernelObjectManipulator;
    }

    @Override
    public KernelObjectMetaManipulator getKernelObjectMetaManipulator() {
        return this.kernelObjectMetaManipulator;
    }
}
