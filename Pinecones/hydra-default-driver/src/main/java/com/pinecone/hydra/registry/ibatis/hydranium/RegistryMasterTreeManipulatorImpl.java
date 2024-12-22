package com.pinecone.hydra.registry.ibatis.hydranium;

import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.registry.ibatis.RegistryNodeOwnerMapper;
import com.pinecone.hydra.registry.ibatis.RegistryNodePathCacheMapper;
import com.pinecone.hydra.registry.ibatis.RegistryTreeMapper;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;

@Component
public class RegistryMasterTreeManipulatorImpl implements TreeMasterManipulator {

    @Resource
    @Structure( type = RegistryNodePathCacheMapper.class )
    TriePathCacheManipulator triePathCacheManipulator;

    @Resource
    @Structure( type = RegistryNodeOwnerMapper.class )
    TireOwnerManipulator tireOwnerManipulator;

    @Resource
    @Structure( type = RegistryTreeMapper.class )
    TrieTreeManipulator  trieTreeManipulator;

    public RegistryMasterTreeManipulatorImpl() {

    }

    public RegistryMasterTreeManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( RegistryMasterTreeManipulatorImpl.class, Map.of(), this );
    }

    @Override
    public TriePathCacheManipulator getTriePathCacheManipulator() {
        return this.triePathCacheManipulator;
    }

    @Override
    public TireOwnerManipulator getTireOwnerManipulator() {
        return this.tireOwnerManipulator;
    }

    @Override
    public TrieTreeManipulator getTrieTreeManipulator() {
        return this.trieTreeManipulator;
    }


}
