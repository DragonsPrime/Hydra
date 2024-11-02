package com.pinecone.hydra.service.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.service.ibatis.AppNodeMetaMapper;
import com.pinecone.hydra.service.ibatis.ApplicationNodeMapper;
import com.pinecone.hydra.service.ibatis.ServiceNamespaceMapper;
import com.pinecone.hydra.service.ibatis.NamespaceRulesMapper;
import com.pinecone.hydra.service.ibatis.ServiceCommonDataMapper;
import com.pinecone.hydra.service.ibatis.ServiceMetaMapper;
import com.pinecone.hydra.service.ibatis.ServiceNodeMapper;
import com.pinecone.hydra.service.ibatis.ServiceNodeOwnerMapper;
import com.pinecone.hydra.service.ibatis.ServiceTreeMapper;
import com.pinecone.hydra.service.kom.source.ApplicationMetaManipulator;
import com.pinecone.hydra.service.kom.source.ApplicationNodeManipulator;
import com.pinecone.hydra.service.kom.source.ServiceNamespaceManipulator;
import com.pinecone.hydra.service.kom.source.NamespaceRulesManipulator;
import com.pinecone.hydra.service.kom.source.CommonDataManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMetaManipulator;
import com.pinecone.hydra.service.kom.source.ServiceNodeManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.udtt.source.TrieTreeManipulator;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Map;
@Component
public class ServiceMasterManipulatorImpl implements ServiceMasterManipulator {

    @Resource
    @Structure(type = ServiceMasterTreeManipulatorImpl.class )
    KOISkeletonMasterManipulator skeletonMasterManipulator;
    @Resource
    @Structure(type = ServiceTreeMapper.class )
    TrieTreeManipulator             trieTreeManipulator;
    @Resource
    @Structure(type = ServiceCommonDataMapper.class )
    CommonDataManipulator           commonDataManipulator;
    @Resource
    @Structure(type = ApplicationNodeMapper.class )
    ApplicationNodeManipulator      applicationNodeManipulator;
    @Resource
    @Structure( type = AppNodeMetaMapper.class )
    ApplicationMetaManipulator      applicationMetaManipulator;
    @Resource
    @Structure( type = ServiceNodeMapper.class )
    ServiceNodeManipulator          serviceNodeManipulator;
    @Resource
    @Structure( type = ServiceMetaMapper.class )
    ServiceMetaManipulator          serviceMetaManipulator;
    @Resource
    @Structure( type = ServiceNamespaceMapper.class )
    ServiceNamespaceManipulator serviceNamespaceManipulator;
    @Resource
    @Structure( type = NamespaceRulesMapper.class )
    NamespaceRulesManipulator namespaceRulesManipulator;

    @Resource
    @Structure( type = ServiceNodeOwnerMapper.class )
    TireOwnerManipulator            tireOwnerManipulator;

    public ServiceMasterManipulatorImpl() {

    }

    public ServiceMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( ServiceMasterManipulatorImpl.class, Map.of(), this );
        this.skeletonMasterManipulator = new ServiceMasterTreeManipulatorImpl( driver );
    }

    @Override
    public TrieTreeManipulator getTrieTreeManipulator() {
        return this.trieTreeManipulator;
    }

    @Override
    public CommonDataManipulator getCommonDataManipulator() {
        return this.commonDataManipulator;
    }

    @Override
    public ApplicationNodeManipulator getApplicationNodeManipulator() {
        return this.applicationNodeManipulator;
    }

    @Override
    public ApplicationMetaManipulator getApplicationElementManipulator() {
        return this.applicationMetaManipulator;
    }

    @Override
    public ServiceNodeManipulator getServiceNodeManipulator() {
        return this.serviceNodeManipulator;
    }

    @Override
    public ServiceMetaManipulator getServiceMetaManipulator() {
        return this.serviceMetaManipulator;
    }

    @Override
    public ServiceNamespaceManipulator getNamespaceManipulator() {
        return this.serviceNamespaceManipulator;
    }

    @Override
    public NamespaceRulesManipulator getNamespaceRulesManipulator() {
        return this.namespaceRulesManipulator;
    }

    @Override
    public TireOwnerManipulator getTireOwnerManipulator() {
        return this.tireOwnerManipulator;
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return this.skeletonMasterManipulator;
    }
}
