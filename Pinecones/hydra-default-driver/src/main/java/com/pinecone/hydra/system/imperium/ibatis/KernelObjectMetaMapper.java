package com.pinecone.hydra.system.imperium.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.imperium.entity.KernelObjectMeta;
import com.pinecone.hydra.system.imperium.source.KernelObjectMetaManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;

@IbatisDataAccessObject
public interface KernelObjectMetaMapper extends KernelObjectMetaManipulator {
    void insert(KernelObjectMeta kernelObjectMeta);
    void remove(GUID guid);
    KernelObjectMeta get(GUID guid);
    void update(KernelObjectMeta kernelObjectMeta);
    KernelObjectMeta getKOMByName(String name);
}
