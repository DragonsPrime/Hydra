package com.pinecone.hydra.account.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.source.UserMasterManipulator;

public abstract class ArchAccountServiceOperator implements AccountServiceOperator {
    protected AccountManager accountManager;

    protected AccountServiceOperatorFactory factory;

    protected DistributedTrieTree       distributedTrieTree;

    protected UserMasterManipulator     userMasterManipulator;

    public ArchAccountServiceOperator(UserMasterManipulator masterManipulator, AccountManager accountManager){
        this.accountManager = accountManager;
        this.userMasterManipulator = masterManipulator;
        this.distributedTrieTree = this.accountManager.getMasterTrieTree();
    }

    protected DistributedTreeNode affirmPreinsertionInitialize(TreeNode node ){
        GUID guid = node.getGuid();
        DistributedTreeNode distributedTreeNode = new GUIDDistributedTrieNode();
        distributedTreeNode.setGuid( guid );
        distributedTreeNode.setType( UOIUtils.createLocalJavaClass( node.getClass().getName() ) );

        return distributedTreeNode;
    }

    public AccountServiceOperatorFactory getUserOperatorFactory(){
        return this.factory;
    }
    protected String getUserMetaType( TreeNode treeNode ){
        return treeNode.className().replace("Generic","");
    }
}
