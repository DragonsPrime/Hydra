package com.pinecone.hydra.system.ko;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.ulf.util.id.GuidAllocator;

public interface KernelObjectInstrument extends Pinenut {
    GuidAllocator getGuidAllocator();

    ImperialTree getMasterTrieTree();

    KernelObjectConfig getConfig();
}
