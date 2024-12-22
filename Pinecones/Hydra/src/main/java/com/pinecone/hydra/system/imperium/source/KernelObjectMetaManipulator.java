package com.pinecone.hydra.system.imperium.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.imperium.entity.KernelObjectMeta;

public interface KernelObjectMetaManipulator {
    void insert(KernelObjectMeta kernelObjectMeta);
    void remove(GUID guid);
    KernelObjectMeta get(GUID guid);
    void update(KernelObjectMeta kernelObjectMeta);
    KernelObjectMeta getKOMByName(String name);
}
