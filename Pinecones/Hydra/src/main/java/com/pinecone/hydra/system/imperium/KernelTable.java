package com.pinecone.hydra.system.imperium;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.imperium.entity.KernelObject;
import com.pinecone.hydra.system.imperium.entity.KernelObjectMeta;

import java.util.List;

public interface KernelTable {
    KernelObjectMeta getKOByGuid(GUID guid);
    List<KernelObject> ListKOByName(String name);
    List<KernelObject> ListKOByGuid(GUID guid);

}
