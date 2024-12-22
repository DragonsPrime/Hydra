package com.pinecone.hydra.system.imperium.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.imperium.entity.KernelObject;
import com.pinecone.hydra.system.imperium.source.KernelObjectManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;

import java.util.List;
@IbatisDataAccessObject
public interface KernelObjectMapper extends KernelObjectManipulator {
    void insert(KernelObject kernelObject);
    void remove(GUID guid, GUID targetGuid);
    void removeAll(GUID guid);
    KernelObject get(GUID guid,GUID targetGuid);
    List<KernelObject> getAll(GUID guid);
    void update(KernelObject kernelObject);
}
