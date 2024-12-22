package com.pinecone.hydra.system.imperium.entity;

import com.pinecone.framework.util.id.GUID;

public interface KernelObjectMeta {
     long getEnumId();
     void setEnumId(long id);
     GUID getGuid();
     void setGuid(GUID guid);
     String getName();
     void setName(String name);
}
