package com.pinecone.hydra.system.ko;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;
import com.pinecone.ulf.util.id.GuidAllocator;

public interface KernelObjectInstrument extends Pinenut {
    GuidAllocator getGuidAllocator();

    DistributedTrieTree getMasterTrieTree();

    KernelObjectConfig getConfig();
}
