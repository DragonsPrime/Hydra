package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface SymbolicMeta extends Pinenut {

    long getEnumId();
    void setEnumId( long enumId );

    GUID getGuid();
    void setGuid(GUID guid);

    void save();
    void remove();
}
