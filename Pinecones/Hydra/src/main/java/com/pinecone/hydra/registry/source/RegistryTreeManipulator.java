package com.pinecone.hydra.registry.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;

import java.util.List;

public interface RegistryTreeManipulator extends Pinenut {
    void insert (DistributedTreeNode distributedConfTreeNode);

    GUIDDistributedTrieNode getNode(GUID guid);

    void remove(GUID guid);

    void removeInheritance(GUID childGuid, GUID parentGuid);

    List<GUID> fetchParentGuids(GUID guid);

    List<GUIDDistributedTrieNode> getChild(GUID guid);
}
