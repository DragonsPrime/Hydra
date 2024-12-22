package com.walnut.sparta.services.drivers;

import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.hydra.service.ibatis.ServiceTreeMapper;
import com.pinecone.hydra.service.ibatis.ServiceNodeOwnerMapper;
import com.pinecone.hydra.service.ibatis.ServicePathCacheMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class ServiceMasterTreeManipulatorImpl implements TreeMasterManipulator {

    @Resource
    ServicePathCacheMapper scopePathManipulator;

    @Resource
    ServiceTreeMapper trieTreeManipulator;

    @Resource
    ServiceNodeOwnerMapper      scopeOwnerManipulator;

    @Override
    public TireOwnerManipulator getTireOwnerManipulator() {
        return this.scopeOwnerManipulator;
    }

    @Override
    public TrieTreeManipulator getTrieTreeManipulator() {
        return this.trieTreeManipulator;
    }

    @Override
    public TriePathCacheManipulator getTriePathCacheManipulator() {
        return this.scopePathManipulator;
    }
}
