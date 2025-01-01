package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;

import java.util.List;

public interface FolderElement extends ElementNode{
    List<ElementNode> fetchChildren();

    List<GUID> fetchChildrenGuids();

    void addChild( ElementNode child );

    boolean containsChild( String childName );
}
