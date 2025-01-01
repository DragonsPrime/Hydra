package com.pinecone.hydra.registry.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;

import java.util.List;

public interface RegistryTreeManipulator extends Pinenut {
    void insert (ImperialTreeNode distributedConfTreeNode);

    GUIDImperialTrieNode getNode(GUID guid);

    void remove(GUID guid);

    void removeInheritance(GUID childGuid, GUID parentGuid);

    List<GUID> fetchParentGuids(GUID guid);

    List<GUIDImperialTrieNode> getChild(GUID guid);
}
