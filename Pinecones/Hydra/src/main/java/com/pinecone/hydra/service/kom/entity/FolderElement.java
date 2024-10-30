package com.pinecone.hydra.service.kom.entity;

import java.util.List;

import com.pinecone.framework.util.id.GUID;

public interface FolderElement extends ElementNode {
    List<ElementNode > fetchChildren();

    List<GUID > fetchChildrenGuids();

    void addChild( ElementNode child );

    boolean containsChild( String childName );
}
