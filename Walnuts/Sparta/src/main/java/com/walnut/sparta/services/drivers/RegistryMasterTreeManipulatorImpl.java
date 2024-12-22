package com.walnut.sparta.services.drivers;

import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.hydra.registry.ibatis.RegistryNodeOwnerMapper;
import com.pinecone.hydra.registry.ibatis.RegistryNodePathCacheMapper;
import com.pinecone.hydra.registry.ibatis.RegistryTreeMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RegistryMasterTreeManipulatorImpl implements TreeMasterManipulator {

    @Resource
    RegistryNodePathCacheMapper configNodePathMapper;

    @Resource
    RegistryNodeOwnerMapper configNodeOwnerManipulator;

    @Resource
    RegistryTreeMapper trieTreeManipulator;

    @Override
    public TireOwnerManipulator getTireOwnerManipulator() {
        return this.configNodeOwnerManipulator;
    }

    @Override
    public TrieTreeManipulator getTrieTreeManipulator() {
        return this.trieTreeManipulator;
    }

    @Override
    public TriePathCacheManipulator getTriePathCacheManipulator() {
        return this.configNodePathMapper;
    }
}
