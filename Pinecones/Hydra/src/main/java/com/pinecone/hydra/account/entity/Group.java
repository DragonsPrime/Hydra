package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;


public interface Group extends TreeNode {
    long getEnumId();
    void setEnumId( long enumId );

    GUID getDefaultPrivilegePolicyGuid();
    void setDefaultPrivilegePolicyGuid( GUID defaultPrivilegePolicyGuid );

    GUID getGuid();
    void setGuid( GUID guid );

    String getName();
    void setName( String name );
}
