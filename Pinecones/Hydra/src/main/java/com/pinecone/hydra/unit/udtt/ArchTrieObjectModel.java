package com.pinecone.hydra.unit.udtt;

import com.pinecone.hydra.system.ko.KernelObjectConfig;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;
import com.pinecone.hydra.unit.udtt.source.TreeMasterManipulator;

public abstract class ArchTrieObjectModel implements KOMInstrument {
    protected DistributedTrieTree   distributedTrieTree;

    protected TreeMasterManipulator treeMasterManipulator;

    protected KernelObjectConfig    kernelObjectConfig;

    public ArchTrieObjectModel( TreeMasterManipulator masterManipulator, KernelObjectConfig kernelObjectConfig ) {
        this.treeMasterManipulator = masterManipulator;  // [1st]
        this.kernelObjectConfig    = kernelObjectConfig; // [2st]
        this.distributedTrieTree   = new GenericDistributedTrieTree( this );
    }

    public ArchTrieObjectModel( KOIMasterManipulator masterManipulator, KernelObjectConfig kernelObjectConfig ) {
        this( (TreeMasterManipulator) masterManipulator.getSkeletonMasterManipulator(), kernelObjectConfig );
    }

    public DistributedTrieTree getMasterTrieTree() {
        return this.distributedTrieTree;
    }

    TreeMasterManipulator getTreeMasterManipulator() {
        return this.treeMasterManipulator;
    }

    public KernelObjectConfig getConfig() {
        return this.kernelObjectConfig;
    }
}
