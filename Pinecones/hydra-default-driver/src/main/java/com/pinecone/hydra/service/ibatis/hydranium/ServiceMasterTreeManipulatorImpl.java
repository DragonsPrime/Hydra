package com.pinecone.hydra.service.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.service.ibatis.ServiceNodeOwnerMapper;
import com.pinecone.hydra.service.ibatis.ServicePathCacheMapper;
import com.pinecone.hydra.service.ibatis.ServiceTreeMapper;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.udtt.source.TreeMasterManipulator;
import com.pinecone.hydra.unit.udtt.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.udtt.source.TrieTreeManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class ServiceMasterTreeManipulatorImpl implements TreeMasterManipulator {
    @Resource
    @Structure( type = ServicePathCacheMapper.class )
    TriePathCacheManipulator triePathCacheManipulator;

    @Resource
    @Structure( type = ServiceNodeOwnerMapper.class )
    TireOwnerManipulator tireOwnerManipulator;

    @Resource
    @Structure( type = ServiceTreeMapper.class )
    TrieTreeManipulator  trieTreeManipulator;

    public ServiceMasterTreeManipulatorImpl() {

    }

    public ServiceMasterTreeManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( ServiceMasterTreeManipulatorImpl.class, Map.of(), this );
    }

    @Override
    public TireOwnerManipulator getTireOwnerManipulator() {
        return this.tireOwnerManipulator;
    }

    @Override
    public TrieTreeManipulator getTrieTreeManipulator() {
        return this.trieTreeManipulator;
    }

    @Override
    public TriePathCacheManipulator getTriePathCacheManipulator() {
        return this.triePathCacheManipulator;
    }
}
