package com.pinecone.hydra.account.operator;

import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.entity.Domain;
import com.pinecone.hydra.account.entity.Group;
import com.pinecone.hydra.unit.imperium.operator.OperatorFactory;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.source.UserMasterManipulator;

public interface AccountServiceOperatorFactory extends OperatorFactory {
    String DefaultUser   = Account.class.getSimpleName();

    String DefaultGroup  = Group.class.getSimpleName();

    String DefaultDomain = Domain.class.getSimpleName();

    void register( String typeName, TreeNodeOperator functionalNodeOperation );

    void registerMetaType( Class<?> clazz, String metaType );

    void registerMetaType( String classFullName, String metaType );

    String getMetaType( String classFullName );

    AccountServiceOperator getOperator(String typeName );

    AccountManager getUserManager();

    UserMasterManipulator getMasterManipulator();
}
