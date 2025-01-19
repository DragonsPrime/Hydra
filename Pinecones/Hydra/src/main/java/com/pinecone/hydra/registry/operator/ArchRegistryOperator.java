package com.pinecone.hydra.registry.operator;

import java.time.LocalDateTime;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.registry.entity.ArchElementNode;
import com.pinecone.hydra.registry.source.RegistryAttributesManipulator;
import com.pinecone.hydra.registry.source.RegistryMasterManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

public abstract class ArchRegistryOperator implements RegistryNodeOperator {
    protected KOMRegistry                    registry;
    protected ImperialTree                   imperialTree;
    protected RegistryMasterManipulator      registryMasterManipulator;
    protected RegistryAttributesManipulator  attributesManipulator;

    protected RegistryOperatorFactory        factory;

    public ArchRegistryOperator ( RegistryOperatorFactory factory ) {
        this( factory.getMasterManipulator(),(KOMRegistry) factory.getRegistry() );
        this.factory = factory;
    }

    public ArchRegistryOperator( RegistryMasterManipulator masterManipulator, KOMRegistry registry ){
        this.registryMasterManipulator     = masterManipulator;
        this.imperialTree = registry.getMasterTrieTree();
        this.attributesManipulator         = this.registryMasterManipulator.getAttributesManipulator();

        this.registry                      = registry;
    }

    protected ImperialTreeNode affirmPreinsertionInitialize(TreeNode treeNode ) {
        ArchElementNode entityNode   = (ArchElementNode) treeNode;

        GUID guid72 = entityNode.getGuid();
        // Case 1: Dummy config node.
        GuidAllocator guidAllocator = this.registry.getGuidAllocator();
        if( guid72 == null ) {
            guid72 = guidAllocator.nextGUID();
            entityNode.setGuid( guid72 );
            entityNode.setCreateTime( LocalDateTime.now() );
        }
        entityNode.setUpdateTime( LocalDateTime.now() );

        ImperialTreeNode imperialTreeNode = new GUIDImperialTrieNode();
        imperialTreeNode.setGuid( guid72 );
        imperialTreeNode.setType( UOIUtils.createLocalJavaClass( entityNode.getClass().getName() ) );

        return imperialTreeNode;
    }

    public RegistryOperatorFactory getOperatorFactory() {
        return this.factory;
    }
}
