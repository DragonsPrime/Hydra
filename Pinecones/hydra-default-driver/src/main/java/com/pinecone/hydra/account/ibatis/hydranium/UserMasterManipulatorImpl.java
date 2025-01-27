package com.pinecone.hydra.account.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.account.ibatis.*;
import com.pinecone.hydra.account.source.*;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
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
    @Structure( type = AuthorizationMapper.class )
    protected AuthorizationManipulator      authorizationManipulator;

    @Resource
    @Structure( type = UserMasterTreeManipulatorImpl.class )
    protected KOISkeletonMasterManipulator skeletonMasterManipulator;

    @Resource
    @Structure( type = CredentialMapper.class )
    protected CredentialManipulator credentialManipulator;
    @Resource
    @Structure( type = PrivilegeMapper.class )
    protected PrivilegeManipulator privilegeManipulator;



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

    @Override
    public CredentialManipulator getCredentialManipulator() {
        return this.credentialManipulator;
    }

    @Override
    public AuthorizationManipulator getAuthorizationManipulator() {
        return this.authorizationManipulator;
    }

    @Override
    public PrivilegeManipulator getPrivilegeManipulator() {
return this.privilegeManipulator;
    }
}
