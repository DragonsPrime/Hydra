package com.pinecone.hydra.storage.volume.policy.strip;

import java.util.List;

public class GenericDynamicStripSizingPolicy implements DynamicStripSizingPolicy {
    protected List<SizingMatcher > mMatchers;
    protected Number               mnDefaultStripSize;

    // Parent

    @Override
    public Number evalStripSize( Number integritySize ) {
        for( SizingMatcher matcher : this.mMatchers ) {
            Number ret = matcher.isMatched( integritySize );
            if( ret != null ) {
                return ret;
            }
        }
        return this.mnDefaultStripSize;
    }

    @Override
    public List<SizingMatcher> getMatchers() {
        return this.mMatchers;
    }

    @Override
    public int getLevelByMatcher( SizingMatcher that ) {
        return this.mMatchers.indexOf( that );
    }

    @Override
    public Number getDefaultStripSize() {
        return this.mnDefaultStripSize;
    }
}
