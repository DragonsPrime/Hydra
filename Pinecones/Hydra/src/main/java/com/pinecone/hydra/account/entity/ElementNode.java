package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

public interface ElementNode extends TreeNode {
    long getEnumId();
    void setEnumId( long enumId );

    String getName();
    void setName( String name );

    GUID getGuid();
    void setGuid( GUID guid );
}
