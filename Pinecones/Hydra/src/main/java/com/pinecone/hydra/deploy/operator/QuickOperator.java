package com.pinecone.hydra.deploy.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;

public class QuickOperator implements TreeNodeOperator {
    @Override
    public GUID insert(TreeNode treeNode) {
        return null;
    }

    @Override
    public void purge(GUID guid) {

    }

    @Override
    public RegistryTreeNode get(GUID guid) {
        return null;
    }

    @Override
    public TreeNode get(GUID guid, int depth) {
        return null;
    }

    @Override
    public RegistryTreeNode getSelf(GUID guid) {
        return null;
    }

    @Override
    public void update( TreeNode treeNode) {

    }

    @Override
    public void updateName(GUID guid, String name) {

    }
}
