package com.pinecone.hydra.file.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.file.ibatis.FileOwnerMapper;
import com.pinecone.hydra.file.ibatis.FilePathCacheMapper;
import com.pinecone.hydra.file.ibatis.FileTreeMapper;
import com.pinecone.hydra.registry.ibatis.RegistryTreeMapper;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMasterTreeManipulatorImpl;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.udtt.source.TreeMasterManipulator;
import com.pinecone.hydra.unit.udtt.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.udtt.source.TrieTreeManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class FileMasterTreeManipulatorImpl implements TreeMasterManipulator {
    @Resource
    @Structure( type = FilePathCacheMapper.class )
    TriePathCacheManipulator triePathCacheManipulator;

    @Resource
    @Structure( type = FileOwnerMapper.class )
    TireOwnerManipulator tireOwnerManipulator;

    @Resource
    @Structure( type = FileTreeMapper.class )
    TrieTreeManipulator trieTreeManipulator;

    public FileMasterTreeManipulatorImpl() {

    }

    public FileMasterTreeManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( FileMasterTreeManipulatorImpl.class, Map.of(), this );
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
