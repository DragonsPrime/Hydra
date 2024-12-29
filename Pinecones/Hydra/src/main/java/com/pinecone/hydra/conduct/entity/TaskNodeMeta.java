package com.pinecone.hydra.conduct.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface TaskNodeMeta extends Pinenut {
    long getEnumId();
    void setEnumId(long id);

    GUID getGuid();
    void setGuid(GUID guid);
}
