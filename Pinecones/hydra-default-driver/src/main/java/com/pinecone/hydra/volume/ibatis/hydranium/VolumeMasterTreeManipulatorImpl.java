package com.pinecone.hydra.volume.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.hydra.volume.ibatis.VolumeCachePathMapper;
import com.pinecone.hydra.volume.ibatis.VolumeOwnerMapper;
import com.pinecone.hydra.volume.ibatis.VolumeTreeMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class VolumeMasterTreeManipulatorImpl implements TreeMasterManipulator {
    @Resource
    @Structure( type = VolumeTreeMapper.class )
    TrieTreeManipulator trieTreeManipulator;

    @Resource
    @Structure( type = VolumeCachePathMapper.class )
    TriePathCacheManipulator triePathCacheManipulator;

    @Resource
    @Structure( type = VolumeOwnerMapper.class )
    TireOwnerManipulator tireOwnerManipulator;

    public VolumeMasterTreeManipulatorImpl() {

    }

    public VolumeMasterTreeManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( VolumeMasterTreeManipulatorImpl.class, Map.of(), this );
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
