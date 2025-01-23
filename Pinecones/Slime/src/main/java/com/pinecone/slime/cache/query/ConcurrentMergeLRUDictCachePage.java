package com.pinecone.slime.cache.query;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.pinecone.framework.unit.Dictium;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.unit.MapDictium;
import com.pinecone.slime.cache.CacheConstants;

/**
 *  Pinecone Ursus For Java [ ConcurrentMergeDictCachePage ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Three-level caching strategy:
 *  L1 Cache: A thread-local cache, achieving the highest performance with lock-free access but is not shareable.
 *  L2 Cache: When a key is evicted from L1, it automatically degrades to L2. With read-write locks,
 *            resulting in a slight performance drop [Lazy merging upwards].
 *  L3 Cache: It will be eliminated, if a key is evicted from L2. No automatic replenishment occurs,
 *            and subsequent access will query external caching services.
 *  This design aims to minimize performance loss caused by locking and accessing external services.
 *  *****************************************************************************************
 *  采用三级缓存设计
 *  其中L1缓存是线程局部缓存（无锁化，性能最高，分治全局不可共享）
 *  当L1中键被淘汰，自动降级到L2，此时使用读写锁，性能有下降但不多 [向上懒汉式归并]
 *  L2缓存再次被淘汰，非升即走，不再自动补充，后面需要访问会访问外部缓存服务
 *  该设计旨在尽可能避免加锁和访问外部服务带来的性能损失。
 *  *****************************************************************************************
 */
public class ConcurrentMergeLRUDictCachePage<V > extends ArchConcurrentCountDictCache<V > implements LocalDictCachePage<V >, IterableDictCachePage<V >, UniformCountSelfLoadingDictCache<V >  {
    private long                                  mnId;
    private final int                             mnCapacity;
    private final Dictium<V >                     mMegaCache;
    private final ReadWriteLock                   mMegaLock;
    private SourceRetriever<V >                   mSourceRetriever;
    private boolean                               mbEnableL2DirectLoad;

    private final ThreadLocal<Map<Object, V > >   mLocalPage;

    protected boolean degradeLocalKey( int size, int capacity, Map.Entry<Object, V > eldest ) {
        boolean bElimination = size > capacity;
        if ( bElimination ) {
            this.mMegaLock.writeLock().lock();
            try{
                // Degrading local-key and merging into mega-L2-cache if the key is ancient enough. (PS, L2-Cache is rw-lock-based then slower)
                // 如果线程独占高速缓存中的键被淘汰，降级并入L2. (二缓有锁，慢)
                this.mMegaCache.insert( eldest.getKey(), eldest.getValue() );
            }
            finally {
                this.mMegaLock.writeLock().unlock();
            }
        }
        return bElimination;
    }

    public ConcurrentMergeLRUDictCachePage( long id, int capacity, int localCap, boolean bUsingTree, boolean bEnableL2DirectLoad, Map<Object, V > initData, SourceRetriever<V > retriever ) {
        super();
        this.mnId                  = id;
        this.mnCapacity            = capacity;
        this.mMegaCache            = new MapDictium<>( LocalFixedLRUDictCachePage.newMap( bUsingTree, capacity, initData ) ) ;
        this.mSourceRetriever      = retriever;
        this.mMegaLock             = new ReentrantReadWriteLock();
        this.mbEnableL2DirectLoad  = bEnableL2DirectLoad;

        this.mLocalPage            = ThreadLocal.withInitial(() -> {
            Map<Object, V > neo;

            if ( bUsingTree ) {
                neo = new LinkedTreeMap<>( true ){
                    @Override
                    protected boolean removeEldestEntry( Map.Entry<Object, V > eldest ) {
                        return ConcurrentMergeLRUDictCachePage.this.degradeLocalKey( this.size(), localCap, eldest );
                    }
                };
            }
            else {
                neo = new LinkedHashMap<>( capacity, 0.75f, true ){
                    @Override
                    protected boolean removeEldestEntry( Map.Entry<Object, V > eldest ) {
                        return ConcurrentMergeLRUDictCachePage.this.degradeLocalKey( this.size(), localCap, eldest );
                    }
                };
            }
            return neo;
        });
    }

    public ConcurrentMergeLRUDictCachePage( long id, int capacity, boolean bUsingTree, boolean bEnableL2DirectLoad, Map<Object, V > initData, SourceRetriever<V > retriever ){
        this( id, capacity, CacheConstants.DefaultCachePageLocalCapacity, bUsingTree, bEnableL2DirectLoad, initData, retriever );
    }

    public ConcurrentMergeLRUDictCachePage( long id, int capacity, int localCap, boolean bUsingTree, Map<Object, V > initData, SourceRetriever<V > retriever ){
        this( id, capacity, localCap, bUsingTree, true, initData, retriever );
    }

    public ConcurrentMergeLRUDictCachePage( long id, int capacity, boolean bUsingTree, Map<Object, V > initData, SourceRetriever<V > retriever ){
        this( id, capacity, CacheConstants.DefaultCachePageLocalCapacity, bUsingTree, true, initData, retriever );
    }

    public ConcurrentMergeLRUDictCachePage( long id, int capacity, SourceRetriever<V > retriever ){
        this( id, capacity, CacheConstants.DefaultCachePageLocalCapacity, false, true, null, retriever );
    }

    public ConcurrentMergeLRUDictCachePage( int capacity, SourceRetriever<V > retriever ){
        this( -1, capacity, retriever );
    }



    public void setEnableL2DirectLoad( boolean bEnableL2DirectLoad ) {
        this.mbEnableL2DirectLoad = bEnableL2DirectLoad;
    }

    @Override
    public long getId() {
        return this.mnId;
    }

    @Override
    public void setId( long id ) {
        this.mnId = id;
    }

    @Override
    public Dictium<V > getDictium() {
        return this.mMegaCache;
    }

    @Override
    public long capacity() {
        return this.mnCapacity;
    }

    @Override
    public long size() {
        this.mMegaLock.readLock().lock();
        try{
            return this.mMegaCache.size();
        }
        finally {
            this.mMegaLock.readLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        this.mMegaLock.readLock().lock();
        try{
            return this.mMegaCache.isEmpty();
        }
        finally {
            this.mMegaLock.readLock().unlock();
        }
    }

    @Override
    public V get( Object key ) {
        V v = this.mLocalPage.get().get( key );
        if ( v == null ) {
            this.mMegaLock.readLock().lock();
            try{
                v = this.mMegaCache.get( key ); // Stage2, try Level-2 Cache retrieving. [Single page is thread-unsafe]
            }
            finally {
                this.mMegaLock.readLock().unlock();
            }

            if( v == null ) {
                // Stage3, try L3 Cache retrieving => L1 . [From superior thread-safe source, e.g. `Redis`, `RDB`]
                //         OR L3 => [ L1, L2 ]
                v = this.missKey( key );
            }
            else {
                this.mLocalPage.get().put( key, v ); // L2 => L1
            }
        }

        this.afterKeyVisited( key );
        return v;
    }

    @Override
    public V erase( Object key ) {
        V v  = this.mLocalPage.get().remove( key );
        V v1;

        this.mMegaLock.writeLock().lock();
        try{
            v1 = this.mMegaCache.erase( key );
        }
        finally {
            this.mMegaLock.writeLock().unlock();
        }

        this.afterKeyVisited( key );

        if ( v == null ) {
            return v1;
        }
        return v;
    }

    @Override
    public boolean existsKey( Object key ) {
        boolean b;
        this.mMegaLock.readLock().lock();
        try{
            b = this.mMegaCache.containsKey( key );
        }
        finally {
            this.mMegaLock.readLock().unlock();
        }

        if ( !b ) {
            b = this.mSourceRetriever.countsKey( key ) > 0;
            if ( b ) {
                // Trigger cache-loading to ensure coherency.
                b = this.get( key ) != null;
            }
        }

        this.afterKeyVisited( key );
        return b;
    }

    @Override
    public boolean implicatesKey( Object key ) {
        return this.existsKey( key );
    }

    @Override
    public SourceRetriever<V> getSourceRetriever() {
        return this.mSourceRetriever;
    }

    @Override
    public void clear() {
        this.mLocalPage.get().clear();

        this.mMegaLock.writeLock().lock();
        try {
            this.mMegaCache.clear();
        }
        finally {
            this.mMegaLock.writeLock().unlock();
        }
    }

    @Override
    public long elementSize() {
        return this.size();
    }

    @Override
    public Set<? > entrySet() {
        return this.getDictium().entrySet();
    }

    @Override
    public Collection<V > values() {
        return this.getDictium().values();
    }

    @Override
    protected V missKey( Object key ) {
        this.recordMiss();
        V v = this.mSourceRetriever.retrieve( key );
        if( v != null ) {
            // L3 => L1
            this.mLocalPage.get().put( key, v );

            if ( this.mbEnableL2DirectLoad ) {
                // L3 => L2
                this.mMegaLock.writeLock().lock();
                try{
                    this.mMegaCache.insert( key, v );
                }
                finally {
                    this.mMegaLock.writeLock().unlock();
                }
            }
        }
        return v;
    }


    public static <V> ConcurrentMergeLRUDictCachePage<V> builder( long id, int capacity, SourceRetriever<V> retriever ) {
        Builder<V > builder = new Builder<>( id, capacity, retriever );
        return builder.build();
    }

    public static <V> ConcurrentMergeLRUDictCachePage<V> builder( int capacity, SourceRetriever<V> retriever ) {
        Builder<V > builder = new Builder<>( capacity, retriever );
        return builder.build();
    }

    public static class Builder<V > {
        private final long                  id;
        private final int                   capacity;
        private final SourceRetriever<V >   retriever;

        private int                         localCap           = CacheConstants.DefaultCachePageLocalCapacity;
        private boolean                     usingTree          = false;
        private boolean                     enableL2DirectLoad = true;
        private Map<Object, V >             initData           = null;


        public Builder( long id, int capacity, SourceRetriever<V> retriever ) {
            this.id        = id;
            this.capacity  = capacity;
            this.retriever = retriever;
        }

        public Builder( int capacity, SourceRetriever<V> retriever ) {
            this( -1, capacity, retriever );
        }


        public Builder<V> localCap( int localCap ) {
            this.localCap = localCap;
            return this;
        }

        public Builder<V> usingTree( boolean bUsingTree ) {
            this.usingTree = bUsingTree;
            return this;
        }

        public Builder<V> enableL2DirectLoad( boolean bEnableL2DirectLoad ) {
            this.enableL2DirectLoad = bEnableL2DirectLoad;
            return this;
        }

        public Builder<V> initData( Map<Object, V> initData ) {
            this.initData = initData;
            return this;
        }

        public ConcurrentMergeLRUDictCachePage<V> build() {
            return new ConcurrentMergeLRUDictCachePage<>(
                    this.id,
                    this.capacity,
                    this.localCap,
                    this.usingTree,
                    this.enableL2DirectLoad,
                    this.initData,
                    this.retriever
            );
        }
    }
}
