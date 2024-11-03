package com.pinecone.hydra.service.kom.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.service.kom.GenericNamespaceRules;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.GenericApplicationElement;
import com.pinecone.hydra.service.kom.entity.GenericNamespace;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.service.kom.source.NamespaceRulesManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.service.kom.source.ServiceNamespaceManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.util.List;

public class NamespaceOperator extends ArchElementOperator implements ElementOperator {
    protected ServiceNamespaceManipulator   namespaceManipulator;
    protected NamespaceRulesManipulator     namespaceRulesManipulator;

    public NamespaceOperator( ElementOperatorFactory factory ) {
        this( factory.getServiceMasterManipulator(),factory.getServicesTree() );
        this.factory = factory;
    }

    public NamespaceOperator( ServiceMasterManipulator masterManipulator, ServicesInstrument servicesInstrument ){
        super( masterManipulator, servicesInstrument);
        this.namespaceManipulator = masterManipulator.getNamespaceManipulator();
        this.namespaceRulesManipulator = masterManipulator.getNamespaceRulesManipulator();
    }

    @Override
    public GUID insert( TreeNode treeNode ) {
        GenericNamespace ns = ( GenericNamespace ) treeNode;

        //存节点基础信息
        GuidAllocator          guidAllocator = this.servicesInstrument.getGuidAllocator();
        GUID              namespaceRulesGuid = ns.getGuid();
        GenericNamespaceRules namespaceRules = ns.getClassificationRules();
        if ( namespaceRules!= null ){
            namespaceRules.setGuid( namespaceRulesGuid );
        }
        else {
            namespaceRulesGuid = null;
        }

        GUID namespaceGuid = guidAllocator.nextGUID72();
        ns.setGuid( namespaceGuid );
        ns.setRulesGUID( namespaceRulesGuid );
        this.namespaceManipulator.insert( ns );

        //存元信息
        GUID metadataGUID = guidAllocator.nextGUID72();
        ns.setMetaGuid( metadataGUID );
        this.commonDataManipulator.insertNS( ns );


        GUIDDistributedTrieNode node = new GUIDDistributedTrieNode();
        node.setBaseDataGUID( namespaceRulesGuid );
        node.setGuid( namespaceGuid );
        node.setNodeMetadataGUID( metadataGUID );
        node.setType( UOIUtils.createLocalJavaClass( treeNode.getClass().getName() ) );
        this.distributedTrieTree.insert( node );
        return namespaceGuid;
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

        if ( node.getType().getObjectName().equals(GenericNamespace.class.getName()) ||  node.getType().getObjectName().equals(GenericApplicationElement.class.getName())){
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
    public Namespace get( GUID guid ) {
        GUIDDistributedTrieNode                    node = this.distributedTrieTree.getNode( guid );
        GenericNamespace                      namespace = new GenericNamespace( this.servicesInstrument );
        GenericNamespaceRules            namespaceRules = this.namespaceRulesManipulator.getNamespaceRules( node.getAttributesGUID() );
        GUIDDistributedTrieNode guidDistributedTrieNode = this.distributedTrieTree.getNode( node.getGuid() );

        if ( namespaceRules != null ){
            namespace.setRulesGUID( namespaceRules.getGuid() );
            namespace.setClassificationRules( namespaceRules );
        }

        GUID metaGuid = guidDistributedTrieNode.getNodeMetadataGUID();
        namespace.setDistributedTreeNode( guidDistributedTrieNode );
        namespace.setName( this.namespaceManipulator.getNamespace( guid ).getName() );
        this.applyCommonMeta( namespace, this.commonDataManipulator.getNodeCommonData( metaGuid ) ); // GUID / MetaGUID difference.
        namespace.setGuid( guid );
        namespace.setMetaGuid( metaGuid );

        return namespace;
    }

    @Override
    public Namespace get( GUID guid, int depth ) {
        return this.get( guid );
    }

    @Override
    public Namespace getSelf( GUID guid ) {
        return this.get( guid );
    }

    @Override
    public void update( TreeNode nodeWideData ) {

    }

    @Override
    public void updateName( GUID guid, String name ) {

    }

    protected void removeNode( GUID guid ){
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        this.distributedTrieTree.purge( guid );
        this.distributedTrieTree.removeCachePath( guid );
        this.namespaceManipulator.remove( node.getGuid() );
        this.namespaceRulesManipulator.remove( node.getNodeMetadataGUID() );
        this.commonDataManipulator.remove( node.getAttributesGUID() );
    }
}
