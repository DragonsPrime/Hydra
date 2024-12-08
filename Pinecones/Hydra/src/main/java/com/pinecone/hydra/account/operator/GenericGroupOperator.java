package com.pinecone.hydra.account.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.entity.Group;
import com.pinecone.hydra.account.source.GroupNodeManipulator;
import com.pinecone.hydra.account.source.UserMasterManipulator;

import java.util.List;

public class GenericGroupOperator extends ArchAccountServiceOperator implements AccountServiceOperator {
    protected GroupNodeManipulator groupNodeManipulator;

    public GenericGroupOperator( AccountServiceOperatorFactory factory ){
        this( factory.getMasterManipulator(), factory.getUserManager() );
        this.groupNodeManipulator = this.userMasterManipulator.getGroupNodeManipulator();
    }

    public GenericGroupOperator(UserMasterManipulator masterManipulator, AccountManager accountManager) {
        super(masterManipulator, accountManager);
        this.groupNodeManipulator = this.userMasterManipulator.getGroupNodeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        Group group = (Group) treeNode;
        DistributedTreeNode distributedTreeNode = this.affirmPreinsertionInitialize(group);
        GUID guid = group.getGuid();

        this.distributedTrieTree.insert( distributedTreeNode );
        this.groupNodeManipulator.insert( group );

        return guid;
    }

    @Override
    public void purge(GUID guid) {
        List<GUIDDistributedTrieNode> children = this.distributedTrieTree.getChildren(guid);
        for( GUIDDistributedTrieNode node : children ){
            TreeNode newInstance = (TreeNode)node.getType().newInstance( new Class<? >[]{this.getClass()}, this );
            AccountServiceOperator operator = this.factory.getOperator(this.getUserMetaType(newInstance));
            operator.purge( node.getGuid() );
        }
        this.removeNode( guid );
    }

    @Override
    public TreeNode get(GUID guid) {
        return this.groupNodeManipulator.queryGroup( guid );
    }

    @Override
    public TreeNode get(GUID guid, int depth) {
        return null;
    }

    @Override
    public TreeNode getSelf(GUID guid) {
        return null;
    }

    @Override
    public void update(TreeNode treeNode) {

    }

    @Override
    public void updateName(GUID guid, String name) {

    }
    private void removeNode( GUID guid ){
        this.distributedTrieTree.purge( guid );
        this.distributedTrieTree.removeCachePath( guid );
        this.groupNodeManipulator.remove( guid );
    }
}
