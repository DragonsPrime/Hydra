package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;


public interface Group extends FolderElement {

    GUID getDefaultPrivilegePolicyGuid();
    void setDefaultPrivilegePolicyGuid( GUID defaultPrivilegePolicyGuid );


}
