package com.pinecone.hydra.system.imperium.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.imperium.entity.KernelObject;

import java.util.List;

public interface KernelObjectManipulator {
    void insert(KernelObject kernelObject);
    void remove(GUID guid,GUID targetGuid);
    void removeAll(GUID guid);
    KernelObject get(GUID guid,GUID targetGuid);
    List<KernelObject> getAll(GUID guid);
    void update(KernelObject kernelObject);
}
