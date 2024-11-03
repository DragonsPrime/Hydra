package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;

public interface LocalClusterMeta {
    long getEnumId();
    void setEnumId(long enumId);

    GUID getGuid();
    void setGuid(GUID guid);

    String getKey();
    void setKey(String key);

    String getValue();
    void setValue( String value );
}
