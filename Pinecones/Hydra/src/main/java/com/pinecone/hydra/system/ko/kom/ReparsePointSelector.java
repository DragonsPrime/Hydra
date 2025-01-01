package com.pinecone.hydra.system.ko.kom;

import com.pinecone.hydra.unit.imperium.entity.ReparseLinkNode;

public interface ReparsePointSelector extends PathSelector {
    ReparseLinkNode searchLinkNode( String[] parts );

    Object search( String[] parts );
}
