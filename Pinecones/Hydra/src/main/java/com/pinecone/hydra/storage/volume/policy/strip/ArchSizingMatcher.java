package com.pinecone.hydra.storage.volume.policy.strip;

public abstract class ArchSizingMatcher implements SizingMatcher {
    protected Number                        mnLevelSize;
    protected DynamicStripSizingPolicy      mSizingPolicy;

    public ArchSizingMatcher( DynamicStripSizingPolicy sizingPolicy, Number levelSize ) {
        this.mSizingPolicy = sizingPolicy;
    }

    public DynamicStripSizingPolicy getSizingPolicy() {
        return this.mSizingPolicy;
    }

    @Override
    public Number getLevelSize() {
        return this.mnLevelSize;
    }

    @Override
    public int getLevel() {
        return this.getSizingPolicy().getLevelByMatcher( this );
    }
}
