package com.pinecone.hydra.account.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.entity.Domain;
import com.pinecone.hydra.account.source.DomainNodeManipulator;
import com.pinecone.hydra.account.source.UserMasterManipulator;

import java.util.List;

public class GenericDomainOperator extends ArchAccountServiceOperator implements AccountServiceOperator {
    protected DomainNodeManipulator  domainNodeManipulator;

    public GenericDomainOperator( AccountServiceOperatorFactory factory ){
        this( factory.getMasterManipulator(), factory.getUserManager() );
        this.domainNodeManipulator = this.userMasterManipulator.getDomainNodeManipulator();
    }
    public GenericDomainOperator(UserMasterManipulator masterManipulator, AccountManager accountManager) {
        super(masterManipulator, accountManager);
        this.domainNodeManipulator = this.userMasterManipulator.getDomainNodeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        Domain domain = (Domain) treeNode;
        DistributedTreeNode distributedTreeNode = this.affirmPreinsertionInitialize(domain);
        GUID guid = domain.getDomainGuid();

        this.distributedTrieTree.insert( distributedTreeNode );
        this.domainNodeManipulator.insert( domain );
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
        return this.domainNodeManipulator.queryDomain( guid );
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
        this.domainNodeManipulator.remove( guid );
    }
}
