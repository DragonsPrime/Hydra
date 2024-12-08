package com.pinecone.hydra.account.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.account.ibatis.DomainNodeMapper;
import com.pinecone.hydra.account.ibatis.GroupNodeMapper;
import com.pinecone.hydra.account.ibatis.UserNodeMapper;
import com.pinecone.hydra.account.source.DomainNodeManipulator;
import com.pinecone.hydra.account.source.GroupNodeManipulator;
import com.pinecone.hydra.account.source.UserMasterManipulator;
import com.pinecone.hydra.account.source.UserNodeManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class UserMasterManipulatorImpl implements UserMasterManipulator {
    @Resource
    @Structure( type = DomainNodeMapper.class )
    protected DomainNodeManipulator         domainNodeManipulator;

    @Resource
    @Structure( type = GroupNodeMapper.class )
    protected GroupNodeManipulator          groupNodeManipulator;

    @Resource
    @Structure( type = UserNodeMapper.class )
    protected UserNodeManipulator           userNodeManipulator;

    @Resource
    @Structure( type = UserMasterTreeManipulatorImpl.class )
    protected KOISkeletonMasterManipulator skeletonMasterManipulator;

    public UserMasterManipulatorImpl() {

    }

    public UserMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( UserMasterManipulatorImpl.class, Map.of(), this );
        this.skeletonMasterManipulator = new UserMasterTreeManipulatorImpl( driver );
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return this.skeletonMasterManipulator;
    }

    @Override
    public DomainNodeManipulator getDomainNodeManipulator() {
        return this.domainNodeManipulator;
    }

    @Override
    public GroupNodeManipulator getGroupNodeManipulator() {
        return this.groupNodeManipulator;
    }

    @Override
    public UserNodeManipulator getUserNodeManipulator() {
        return this.userNodeManipulator;
    }
}
