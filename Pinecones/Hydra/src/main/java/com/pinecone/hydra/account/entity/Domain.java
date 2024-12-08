package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

public interface Domain extends TreeNode {
    Long getEnumId();
    void setEnumId( long enumId );

    String getDomainName();
    void setDomainName( String domainName );

    GUID getDomainGuid();
    void setDomainGuid( GUID domainGuid );

    String getName();
    void setName( String name );
}
