package com.pinecone.hydra.scenario.tree;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

public interface DistributedScenarioMetaTree extends Pinenut {
    String getPath(GUID guid);

    GUID insert(TreeNode treeNode);

    TreeNode get(GUID guid);

    TreeNode parsePath(String path);

    void remove(GUID guid);

    TreeNode getSelf(GUID guid);
}
