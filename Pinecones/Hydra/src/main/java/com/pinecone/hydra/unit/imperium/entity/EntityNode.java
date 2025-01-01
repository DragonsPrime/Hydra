package com.pinecone.hydra.unit.imperium.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface EntityNode extends Pinenut {
    String getName();

    GUID getGuid();
}
