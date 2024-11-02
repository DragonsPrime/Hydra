package com.pinecone.hydra.service.kom.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.service.kom.ServiceFamilyNode;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.GenericApplicationElement;
import com.pinecone.hydra.service.kom.entity.ServiceTreeNode;
import com.pinecone.hydra.service.kom.source.ApplicationMetaManipulator;
import com.pinecone.hydra.service.kom.source.ApplicationNodeManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.ulf.util.id.GUIDs;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.util.List;

public class ApplicationElementOperator extends ArchElementOperator implements ElementOperator {
    protected ApplicationNodeManipulator        applicationNodeManipulator;
    protected ApplicationMetaManipulator        applicationMetaManipulator;

    public ApplicationElementOperator(ElementOperatorFactory factory ) {
        this( factory.getServiceMasterManipulator(),factory.getServicesTree() );
        this.factory = factory;
    }

    public ApplicationElementOperator(ServiceMasterManipulator masterManipulator, ServicesInstrument servicesInstrument ){
        super( masterManipulator, servicesInstrument);
        this.applicationNodeManipulator = masterManipulator.getApplicationNodeManipulator();
        this.applicationMetaManipulator = masterManipulator.getApplicationElementManipulator();
    }


    @Override
    public GUID insert( TreeNode treeNode ) {
        GenericApplicationElement applicationElement = (GenericApplicationElement) treeNode;

        GuidAllocator guidAllocator = this.servicesInstrument.getGuidAllocator();
        GUID applicationNodeGUID = guidAllocator.nextGUID72();
        applicationElement.setGuid( applicationNodeGUID );
        this.applicationNodeManipulator.insert( applicationElement );


        GUID descriptionGUID = guidAllocator.nextGUID72();
        if( applicationElement.getMetaGuid() == null ){
            applicationElement.setMetaGuid( descriptionGUID );
        }
        this.applicationMetaManipulator.insert( applicationElement );


        //将应用元信息存入元信息表
        this.commonDataManipulator.insert( applicationElement );


        //将节点信息存入主表
        GUIDDistributedTrieNode node = new GUIDDistributedTrieNode();
        node.setNodeMetadataGUID(descriptionGUID);
        node.setGuid(applicationNodeGUID);
        node.setType( UOIUtils.createLocalJavaClass( treeNode.getClass().getName() ) );
        this.distributedTrieTree.insert( node );
        return applicationNodeGUID;
    }


    @Override
    public void purge( GUID guid ) {
        //namespace节点需要递归删除其拥有节点若其引用节点，没有其他引用则进行清理
        List<GUIDDistributedTrieNode> childNodes = this.distributedTrieTree.getChildren(guid);
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        if ( !childNodes.isEmpty() ){
            List<GUID > subordinates = this.distributedTrieTree.getSubordinates(guid);
            if ( !subordinates.isEmpty() ){
                for ( GUID subordinateGuid : subordinates ){
                    this.purge( subordinateGuid );
                }
            }
            childNodes = this.distributedTrieTree.getChildren( guid );
            for( GUIDDistributedTrieNode childNode : childNodes ){
                List<GUID > parentNodes = this.distributedTrieTree.fetchParentGuids(childNode.getGuid());
                if ( parentNodes.size() > 1 ){
                    this.distributedTrieTree.removeInheritance(childNode.getGuid(),guid);
                }
                else {
                    this.purge( childNode.getGuid() );
                }
            }
        }

        if ( node.getType().getObjectName().equals(com.pinecone.hydra.registry.entity.GenericNamespace.class.getName()) ){
            this.removeNode(guid);
        }
        else {
            UOI uoi = node.getType();
            String metaType = this.getOperatorFactory().getMetaType( uoi.getObjectName() );
            if( metaType == null ) {
                TreeNode newInstance = (TreeNode)uoi.newInstance( new Class<? >[]{ ServicesInstrument.class }, this.servicesInstrument);
                metaType = newInstance.getMetaType();
            }

            ElementOperator operator = this.getOperatorFactory().getOperator( metaType );
            operator.purge( guid );
        }
    }

    @Override
    public ApplicationElement get( GUID guid ) {
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode( guid );
        ApplicationElement applicationElement;
        if( node.getNodeMetadataGUID() != null ){
            applicationElement = this.applicationMetaManipulator.getApplicationElement( node.getNodeMetadataGUID(), this.servicesInstrument );
        }
        else {
            applicationElement = new GenericApplicationElement();
        }

        this.applyCommonMeta( applicationElement, this.commonDataManipulator.getNodeCommonData( guid ) );

        applicationElement.setName( this.applicationNodeManipulator.getApplicationNode(guid).getName() );
        applicationElement.setGuid(applicationElement.getGuid());
        return applicationElement;
    }

    @Override
    public ApplicationElement get( GUID guid, int depth ) {
        return this.get( guid );
    }

    @Override
    public ApplicationElement getSelf( GUID guid) {
        return this.get( guid );
    }

    @Override
    public void update( TreeNode treeNode ) {

    }

    @Override
    public void updateName( GUID guid, String name ) {

    }

    protected void removeNode( GUID guid ){
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode( guid );
        this.distributedTrieTree.purge( guid );
        this.distributedTrieTree.removeCachePath(guid);
        this.applicationMetaManipulator.remove( node.getAttributesGUID() );
        this.commonDataManipulator.remove( node.getNodeMetadataGUID() );
        this.applicationNodeManipulator.remove( node.getGuid( ));
    }
}
