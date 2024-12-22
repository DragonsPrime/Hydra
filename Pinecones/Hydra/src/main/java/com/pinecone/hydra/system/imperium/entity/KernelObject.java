package com.pinecone.hydra.system.imperium.entity;

import com.pinecone.framework.util.id.GUID;

public interface KernelObject {
    long getEnumId();
    void setEnumId(long id);
    GUID getGuid();
    void setGuid(GUID guid);
    GUID getTargetGuid();
    void setTargetGuid(GUID targetGuid);
    String getTargetType();
    void setTargetType(String type);
    String getTargetSimpleType();
    void setTargetSimpleType(String targetSimpleType);
}
