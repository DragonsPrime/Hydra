package com.pinecone.hydra.account.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.source.UserMasterManipulator;
import com.pinecone.hydra.account.source.UserNodeManipulator;

import java.time.LocalDateTime;
import java.util.List;

public class GenericAccountOperator extends ArchAccountServiceOperator implements AccountServiceOperator {
    protected UserNodeManipulator   userNodeManipulator;

    public GenericAccountOperator(AccountServiceOperatorFactory factory ){
        this( factory.getMasterManipulator(), factory.getUserManager() );
        this.userNodeManipulator = this.userMasterManipulator.getUserNodeManipulator();
    }

    public GenericAccountOperator(UserMasterManipulator masterManipulator, AccountManager accountManager) {
        super(masterManipulator, accountManager);
        this.userNodeManipulator = this.userMasterManipulator.getUserNodeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        Account account = (Account) treeNode;
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());
        ImperialTreeNode imperialTreeNode = this.affirmPreinsertionInitialize(account);
        GUID guid = account.getGuid();

        this.imperialTree.insert(imperialTreeNode);
        this.userNodeManipulator.insert(account);

        return guid;
    }

    @Override
    public void purge(GUID guid) {
        List<GUIDImperialTrieNode> children = this.imperialTree.getChildren(guid);
        for( GUIDImperialTrieNode node : children ){
            TreeNode newInstance = (TreeNode)node.getType().newInstance( new Class<? >[]{this.getClass()}, this );
            AccountServiceOperator operator = this.factory.getOperator(this.getUserMetaType(newInstance));
            operator.purge( node.getGuid() );
        }
        this.removeNode( guid );
    }

    @Override
    public TreeNode get(GUID guid) {
        return this.userNodeManipulator.queryUser(guid);
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
        this.imperialTree.purge( guid );
        this.imperialTree.removeCachePath( guid );
        this.userNodeManipulator.remove( guid );
    }
}
