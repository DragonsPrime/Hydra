package com.pinecone.hydra.storage.volume.policy.strip;

public class TinyFileSizingMatcher64 extends ArchSizingMatcher {
    public TinyFileSizingMatcher64( DynamicStripSizingPolicy sizingPolicy, Number levelSize ) {
        super( sizingPolicy, levelSize );
    }

    @Override
    public Number isMatched( Number integritySize ) {
        long i64IntegritySize = integritySize.longValue();
        if( i64IntegritySize <= 1024 * 1024 * 1024 ) { // [0, 1G]
            return this.getLevelSize();
        }

        return null;
    }

}
