package com.pinecone.hydra.account.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.hydra.account.ibatis.UserOwnerMapper;
import com.pinecone.hydra.account.ibatis.UserPathCacheMapper;
import com.pinecone.hydra.account.ibatis.UserTreeMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class UserMasterTreeManipulatorImpl implements TreeMasterManipulator {
    @Resource
    @Structure( type = UserOwnerMapper.class )
    protected TireOwnerManipulator          tireOwnerManipulator;

    @Resource
    @Structure( type = UserTreeMapper.class )
    protected TrieTreeManipulator           trieTreeManipulator;

    @Resource
    @Structure( type = UserPathCacheMapper.class )
    protected TriePathCacheManipulator      triePathCacheManipulator;

    public UserMasterTreeManipulatorImpl() {

    }

    public UserMasterTreeManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( UserMasterTreeManipulatorImpl.class, Map.of(), this );
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
