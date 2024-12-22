package com.pinecone.hydra.registry.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;

public interface RegistryNodeOperator extends TreeNodeOperator {
    @Override
    RegistryTreeNode get( GUID guid );

    RegistryTreeNode get( GUID guid, int depth );

    @Override
    RegistryTreeNode getSelf( GUID guid );
}
