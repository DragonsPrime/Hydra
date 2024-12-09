package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;


public interface Group extends ElementNode {

    GUID getDefaultPrivilegePolicyGuid();
    void setDefaultPrivilegePolicyGuid( GUID defaultPrivilegePolicyGuid );


}
