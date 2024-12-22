package com.pinecone.hydra.unit.imperium;

import com.pinecone.hydra.system.ko.KernelObjectConfig;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;

public abstract class ArchRegimentObjectModel implements KOMInstrument {
    protected ImperialTree          imperialTree;

    protected TreeMasterManipulator treeMasterManipulator;

    protected KernelObjectConfig    kernelObjectConfig;

    public ArchRegimentObjectModel( TreeMasterManipulator masterManipulator, KernelObjectConfig kernelObjectConfig ) {
        this.treeMasterManipulator = masterManipulator;  // [1st]
        this.kernelObjectConfig    = kernelObjectConfig; // [2st]
        this.imperialTree = new RegimentedImperialTree( this );
    }

    public ArchRegimentObjectModel( KOIMasterManipulator masterManipulator, KernelObjectConfig kernelObjectConfig ) {
        this( (TreeMasterManipulator) masterManipulator.getSkeletonMasterManipulator(), kernelObjectConfig );
    }

    public ImperialTree getMasterTrieTree() {
        return this.imperialTree;
    }

    TreeMasterManipulator getTreeMasterManipulator() {
        return this.treeMasterManipulator;
    }

    public KernelObjectConfig getConfig() {
        return this.kernelObjectConfig;
    }
}
