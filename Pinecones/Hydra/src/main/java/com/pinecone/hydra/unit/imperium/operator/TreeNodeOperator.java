package com.pinecone.hydra.unit.imperium.operator;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

public interface TreeNodeOperator extends Pinenut {
    GUID insert( TreeNode treeNode );

    void purge( GUID guid );

    TreeNode get( GUID guid ) ;

    TreeNode get( GUID guid, int depth );

    TreeNode getSelf( GUID guid );

    void update( TreeNode treeNode );

    void updateName( GUID guid ,String name );
}
