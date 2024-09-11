package com.pinecone.hydra.scenario.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface NamespaceNode extends Pinenut {
    int getEnumId();
    void setEnumId(int id);

    GUID getGuid();
    void setGuid(GUID guid);

    String getName();
    void setName(String name);
}
