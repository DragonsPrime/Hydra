package com.pinecone.hydra.service.kom.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.GenericServiceElement;
import com.pinecone.hydra.service.kom.entity.ServiceElement;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMetaManipulator;
import com.pinecone.hydra.service.kom.source.ServiceNodeManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

public class ServiceElementOperator extends ArchElementOperator implements ElementOperator {
    protected ServiceNodeManipulator  serviceNodeManipulator;
    protected ServiceMetaManipulator  serviceMetaManipulator;

    public ServiceElementOperator( ElementOperatorFactory factory ) {
        this( factory.getServiceMasterManipulator(),factory.getServicesTree() );
        this.factory = factory;
    }

    public ServiceElementOperator( ServiceMasterManipulator masterManipulator, ServicesInstrument servicesInstrument ){
        super( masterManipulator, servicesInstrument);
       this.serviceNodeManipulator = masterManipulator.getServiceNodeManipulator();
       this.serviceMetaManipulator = masterManipulator.getServiceMetaManipulator();

    }


    @Override
    public GUID insert( TreeNode treeNode ) {
        GenericServiceElement serviceElement = (GenericServiceElement) treeNode;

        //将信息写入数据库
        //将节点信息存入应用节点表
        GuidAllocator guidAllocator = this.servicesInstrument.getGuidAllocator();
        GUID serviceNodeGUID = guidAllocator.nextGUID();
        serviceElement.setGuid(serviceNodeGUID);
        this.serviceNodeManipulator.insert( serviceElement );

        //将应用节点基础信息存入信息表
        GUID metaGUID = guidAllocator.nextGUID();
        if ( serviceElement.getMetaGuid() == null ){
            serviceElement.setMetaGuid( metaGUID );
        }
        this.serviceMetaManipulator.insert( serviceElement );


        //将应用元信息存入元信息表
       this.commonDataManipulator.insert( serviceElement );


        //将节点信息存入主表
        GUIDImperialTrieNode node = new GUIDImperialTrieNode();
        node.setNodeMetadataGUID( metaGUID );
        node.setGuid( serviceNodeGUID );
        node.setType( UOIUtils.createLocalJavaClass( treeNode.getClass().getName() ) );
        this.imperialTree.insert( node );
        return serviceNodeGUID;
    }

    @Override
    public void purge( GUID guid ) {
        this.removeNode( guid );
    }

    @Override
    public ServiceElement get( GUID guid ) {
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        ServiceElement serviceElement = new GenericServiceElement();
        if( node.getNodeMetadataGUID() != null ){
            serviceElement = this.serviceMetaManipulator.getServiceMeta( node.getNodeMetadataGUID() );
        }

        this.applyCommonMeta( serviceElement, this.commonDataManipulator.getNodeCommonData( guid ) );

        serviceElement.setDistributedTreeNode(node);
        serviceElement.setGuid( guid );
        serviceElement.setName( this.serviceNodeManipulator.getServiceNode(guid).getName() );

        return serviceElement;
    }

    @Override
    public ServiceElement get( GUID guid, int depth ) {
        return this.get( guid );
    }

    @Override
    public ServiceElement getSelf( GUID guid ) {
        return this.get( guid );
    }

    @Override
    public void update( TreeNode nodeWideData ) {
        GenericServiceElement serviceElement = (GenericServiceElement) nodeWideData;
        this.serviceNodeManipulator.update( serviceElement );
        this.serviceMetaManipulator.update( serviceElement );
        this.commonDataManipulator.update( serviceElement );
    }

    @Override
    public void updateName(GUID guid, String name) {

    }

    private void removeNode( GUID guid ){
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        this.imperialTree.purge( guid );
        this.imperialTree.removeCachePath( guid );
        this.serviceNodeManipulator.remove( node.getGuid() );
        this.serviceMetaManipulator.remove( node.getAttributesGUID() );
        this.commonDataManipulator.remove( node.getNodeMetadataGUID() );
    }
}
