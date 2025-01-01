package com.pinecone.hydra.unit.imperium.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.LinkedType;

public interface ReparseLinkNode extends EntityNode {
    @Override
    default String getName() {
        return this.getTagName();
    }

    @Override
    default GUID getGuid() {
        return this.getTagGuid();
    }

    String getTagName();

    GUID getTagGuid();

    LinkedType getLinkedType();

    GUID getTargetNodeGuid();

    GUID getParentNodeGuid();
}
