package com.pinecone.hydra.storage.volume.policy.strip;

public class MegaFileSizingMatcher64 extends ArchSizingMatcher {
    public MegaFileSizingMatcher64( DynamicStripSizingPolicy sizingPolicy, Number levelSize ) {
        super( sizingPolicy, levelSize );
    }

    @Override
    public Number isMatched( Number integritySize ) {
        long i64IntegritySize = integritySize.longValue();
        if( i64IntegritySize > 100L * 1024 * 1024 * 1024 ) { // (100GB, +∞]
            return this.getLevelSize();
        }

        return null;
    }
}
