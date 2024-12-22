package com.pinecone.hydra.task.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.task.ibatis.TaskNodeOwnerMapper;
import com.pinecone.hydra.task.ibatis.TaskNodePathCacheMapper;
import com.pinecone.hydra.task.ibatis.TaskTreeMapper;
import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class TaskMasterTreeManipulatorImpl implements TreeMasterManipulator {
    @Resource
    @Structure( type = TaskNodePathCacheMapper.class )
    TriePathCacheManipulator triePathCacheManipulator;

    @Resource
    @Structure( type = TaskNodeOwnerMapper.class )
    TireOwnerManipulator tireOwnerManipulator;

    @Resource
    @Structure( type = TaskTreeMapper.class )
    TrieTreeManipulator  trieTreeManipulator;

    public TaskMasterTreeManipulatorImpl() {

    }

    public TaskMasterTreeManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( TaskMasterTreeManipulatorImpl.class, Map.of(), this );
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
