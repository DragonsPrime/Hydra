package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

public interface Domain extends FolderElement {
    String getDomainName();
    void setDomainName( String domainName );
}
