package com.pinecone.hydra.service.kom.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.GenericApplicationElement;
import com.pinecone.hydra.service.kom.source.ApplicationMetaManipulator;
import com.pinecone.hydra.service.kom.source.ApplicationNodeManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
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
        GUIDImperialTrieNode node = new GUIDImperialTrieNode();
        node.setNodeMetadataGUID(descriptionGUID);
        node.setGuid(applicationNodeGUID);
        node.setType( UOIUtils.createLocalJavaClass( treeNode.getClass().getName() ) );
        this.imperialTree.insert( node );
        return applicationNodeGUID;
    }


    @Override
    public void purge( GUID guid ) {
        //namespace节点需要递归删除其拥有节点若其引用节点，没有其他引用则进行清理
        List<GUIDImperialTrieNode> childNodes = this.imperialTree.getChildren(guid);
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        if ( !childNodes.isEmpty() ){
            List<GUID > subordinates = this.imperialTree.getSubordinates(guid);
            if ( !subordinates.isEmpty() ){
                for ( GUID subordinateGuid : subordinates ){
                    this.purge( subordinateGuid );
                }
            }
            childNodes = this.imperialTree.getChildren( guid );
            for( GUIDImperialTrieNode childNode : childNodes ){
                List<GUID > parentNodes = this.imperialTree.fetchParentGuids(childNode.getGuid());
                if ( parentNodes.size() > 1 ){
                    this.imperialTree.removeInheritance(childNode.getGuid(),guid);
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
        GUIDImperialTrieNode node = this.imperialTree.getNode( guid );
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
    public ApplicationElement getSelf( GUID guid ) {
        return this.get( guid );
    }

    @Override
    public void update( TreeNode treeNode ) {
        GenericApplicationElement applicationElement = (GenericApplicationElement) treeNode;
        this.applicationNodeManipulator.update( applicationElement );
        this.applicationMetaManipulator.update( applicationElement );
        this.commonDataManipulator.update( applicationElement );
    }

    @Override
    public void updateName( GUID guid, String name ) {

    }

    protected void removeNode( GUID guid ){
        GUIDImperialTrieNode node = this.imperialTree.getNode( guid );
        this.imperialTree.purge( guid );
        this.imperialTree.removeCachePath(guid);
        this.applicationMetaManipulator.remove( node.getAttributesGUID() );
        this.commonDataManipulator.remove( node.getNodeMetadataGUID() );
        this.applicationNodeManipulator.remove( node.getGuid( ));
    }
}
