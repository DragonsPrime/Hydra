package com.pinecone.hydra.storage.volume.policy.strip;

public class SmallFileSizingMatcher64 extends ArchSizingMatcher {
    public SmallFileSizingMatcher64( DynamicStripSizingPolicy sizingPolicy, Number levelSize ) {
        super( sizingPolicy, levelSize );
    }

    @Override
    public Number isMatched( Number integritySize ) {
        long i64IntegritySize = integritySize.longValue();
        if( i64IntegritySize > 1024 * 1024 * 1024 && i64IntegritySize <= 100L * 1024 * 1024 * 1024 ) { // (1GB, 100GB]
            return this.getLevelSize();
        }

        return null;
    }
}
