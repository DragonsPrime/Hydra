package com.pinecone.slime.cache.query;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ArchConcurrentCountDictCache<V > implements UniformCountDictCache<V > {
    protected AtomicLong mnMisses;
    protected AtomicLong mnAccesses;

    protected ArchConcurrentCountDictCache(){
        this.mnMisses   = new AtomicLong( 0 );
        this.mnAccesses = new AtomicLong( 0 );
    }

    protected void afterKeyVisited( Object key ) {
        this.recordAccess();
    }

    protected abstract V missKey( Object key ) ;

    protected void recordMiss() {
        this.mnMisses.incrementAndGet();
    }

    protected void recordAccess() {
        this.mnAccesses.incrementAndGet();
    }

    @Override
    public long getMisses() {
        return this.mnMisses.get();
    }

    @Override
    public long getAccesses() {
        return this.mnAccesses.get();
    }
}