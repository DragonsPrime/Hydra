package com.pinecone.hydra.account.operator;

import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.source.UserMasterManipulator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GenericAccountOperatorFactory implements AccountServiceOperatorFactory {

    protected UserMasterManipulator             userMasterManipulator;

    protected AccountManager accountManager;

    protected Map<String, TreeNodeOperator>     registerer = new HashMap<>();
    protected Map<String, String >              metaTypeMap = new TreeMap<>();

   public GenericAccountOperatorFactory(AccountManager accountManager, UserMasterManipulator userMasterManipulator ){
       this.accountManager = accountManager;
       this.userMasterManipulator = userMasterManipulator;

       this.registerer.put(
               DefaultUser,
               new GenericAccountOperator( this )
       );
       this.registerer.put(
               DefaultGroup,
               new GenericGroupOperator( this )
       );
       this.registerer.put(
               DefaultDomain,
               new GenericDomainOperator( this )
       );

   }

    @Override
    public void register(String typeName, TreeNodeOperator functionalNodeOperation) {
        this.registerer.put( typeName, functionalNodeOperation );
    }

    @Override
    public void registerMetaType(Class<?> clazz, String metaType) {
        this.registerMetaType( clazz.getName(), metaType );
    }

    @Override
    public void registerMetaType(String classFullName, String metaType) {
        this.metaTypeMap.put( classFullName, metaType );
    }

    @Override
    public String getMetaType(String classFullName) {
        return this.metaTypeMap.get( classFullName );
    }

    @Override
    public AccountServiceOperator getOperator(String typeName) {
        return (AccountServiceOperator) this.registerer.get( typeName );
    }

    @Override
    public AccountManager getUserManager() {
        return this.accountManager;
    }

    @Override
    public UserMasterManipulator getMasterManipulator() {
        return this.userMasterManipulator;
    }
}
