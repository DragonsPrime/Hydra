package com.pinecone.hydra.registry.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.registry.entity.GenericNamespace;
import com.pinecone.hydra.registry.entity.Namespace;
import com.pinecone.hydra.registry.entity.NamespaceMeta;
import com.pinecone.hydra.registry.entity.Attributes;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.registry.source.RegistryMasterManipulator;
import com.pinecone.hydra.registry.source.RegistryNSNodeManipulator;
import com.pinecone.hydra.registry.source.RegistryNSNodeMetaManipulator;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.util.ArrayList;
import java.util.List;

public class NamespaceNodeOperator extends ArchRegistryOperator {
    private RegistryNSNodeManipulator     namespaceNodeManipulator;
    private RegistryNSNodeMetaManipulator namespaceNodeMetaManipulator;


    public NamespaceNodeOperator ( RegistryOperatorFactory factory ) {
        this( factory.getMasterManipulator(),(KOMRegistry) factory.getRegistry() );
        this.factory = factory;
    }

    public NamespaceNodeOperator( RegistryMasterManipulator masterManipulator , KOMRegistry registry ){
        super( masterManipulator, registry );
        this.namespaceNodeManipulator       = this.registryMasterManipulator.getNSNodeManipulator();
        this.namespaceNodeMetaManipulator   = this.registryMasterManipulator.getNSNodeMetaManipulator();
    }

    @Override
    public GUID insert( TreeNode treeNode ) {
        Namespace nsNode        = (Namespace) treeNode;
        DistributedTreeNode distributedTreeNode = this.affirmPreinsertionInitialize( treeNode );
        GuidAllocator guidAllocator = this.registry.getGuidAllocator();
        GUID guid72                 = nsNode.getGuid();

        NamespaceMeta namespaceMeta = nsNode.getNamespaceWithMeta();
        GUID namespaceNodeMetaGuid = guidAllocator.nextGUID72();
        if (namespaceMeta != null){
            namespaceMeta.setGuid(namespaceNodeMetaGuid);
            this.namespaceNodeMetaManipulator.insert(namespaceMeta);
        }
        else {
            namespaceNodeMetaGuid = null;
        }


        Attributes attributes = nsNode.getAttributes();
        GUID nodeAttributesGuid = guidAllocator.nextGUID72();
        if (attributes != null){
            attributes.setGuid( nodeAttributesGuid );
            this.attributesManipulator.insert(attributes);
        }
        else {
            nodeAttributesGuid = null;
        }

        distributedTreeNode.setNodeMetadataGUID(namespaceNodeMetaGuid);
        distributedTreeNode.setBaseDataGUID(nodeAttributesGuid);
        this.distributedTrieTree.insert( distributedTreeNode );
        this.namespaceNodeManipulator.insert( nsNode );
        return guid72;
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

        if ( node.getType().getObjectName().equals(GenericNamespace.class.getName()) ){
            this.removeNode(guid);
        }
        else {
            UOI uoi = node.getType();
            String metaType = this.getOperatorFactory().getMetaType( uoi.getObjectName() );
            if( metaType == null ) {
                TreeNode newInstance = (TreeNode)uoi.newInstance( new Class<? >[]{ KOMRegistry.class }, this.registry );
                metaType = newInstance.getMetaType();
            }

            RegistryNodeOperator operator = this.getOperatorFactory().getOperator( metaType );
            operator.purge( guid );
        }
    }

    @Override
    public RegistryTreeNode get( GUID guid ) {
        return this.getNamespaceNodeWideData( guid, 0 );
    }

    @Override
    public RegistryTreeNode get( GUID guid, int depth ) {
        return this.getNamespaceNodeWideData( guid, depth );
    }

    @Override
    public RegistryTreeNode getSelf( GUID guid ) {
        return this.getNamespaceNodeWideData( guid, 0 );
    }

    @Override
    public void update( TreeNode treeNode ) {

    }

    @Override
    public void updateName( GUID guid, String name ) {
        this.namespaceNodeManipulator.updateName( guid, name );
    }

    private Namespace getNamespaceNodeWideData( GUID guid, int depth ){
        Namespace ns = this.namespaceNodeManipulator.getNamespaceWithMeta( guid );
        if ( ns instanceof GenericNamespace ){
             ((GenericNamespace) ns).apply( this.registry );
        }
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);

        if( depth <= 0 ) {
            List<GUIDDistributedTrieNode> childNode = this.distributedTrieTree.getChildren(guid);
            ArrayList<GUID> guids = new ArrayList<>();
            for ( GUIDDistributedTrieNode n : childNode ){
                guids.add( n.getGuid() );
            }
            ++depth;
            ns.setChildrenGuids( guids, depth );
        }

        Attributes           attributes = this.attributesManipulator.getAttributes( guid, ns );
        NamespaceMeta namespaceNodeMeta = this.namespaceNodeMetaManipulator.getNamespaceNodeMeta( node.getNodeMetadataGUID() );
        ns.setAttributes    ( attributes );
        ns.setNamespaceMeta ( namespaceNodeMeta );
        return ns;
    }

    private void removeNode( GUID guid ){
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        this.distributedTrieTree.purge( guid );
        this.distributedTrieTree.removeCachePath(guid);
        this.namespaceNodeManipulator.remove(guid);
        this.namespaceNodeMetaManipulator.remove(node.getNodeMetadataGUID());
        this.attributesManipulator.remove(node.getAttributesGUID());
    }

}
