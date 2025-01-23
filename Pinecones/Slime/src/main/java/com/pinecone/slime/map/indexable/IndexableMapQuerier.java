package com.pinecone.slime.map.indexable;

import com.pinecone.framework.system.NotImplementedException;
import com.pinecone.slime.cache.CacheConstants;
import com.pinecone.slime.cache.query.ConcurrentMergeLRUDictCachePage;
import com.pinecone.slime.cache.query.LocalFixedLRUDictCachePage;
import com.pinecone.slime.cache.query.UniformCountSelfLoadingDictCache;
import com.pinecone.slime.map.AlterableCacher;
import com.pinecone.slime.source.indexable.GenericIndexKeySourceRetriever;
import com.pinecone.slime.source.indexable.IndexableDataManipulator;
import com.pinecone.slime.source.indexable.IndexableIterableManipulator;
import com.pinecone.slime.source.indexable.IndexableTargetScopeMeta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class IndexableMapQuerier<K, V > implements AlterableCacher<V > {
    private final IndexableDataManipulator<K, V >  mManipulator;
    protected UniformCountSelfLoadingDictCache<V > mCache;
    protected IndexableTargetScopeMeta             mIndexMeta;

    protected static <V > UniformCountSelfLoadingDictCache<V > newCache( IndexableTargetScopeMeta meta, boolean bConcurrent ) {
        if ( bConcurrent ) {
            return new ConcurrentMergeLRUDictCachePage<>( CacheConstants.DefaultCachePageMegaCapacity,
                    new GenericIndexKeySourceRetriever<>( meta )
            );
        }
        else {
            return new LocalFixedLRUDictCachePage<>( CacheConstants.DefaultCachePageMegaCapacity,
                    new GenericIndexKeySourceRetriever<>( meta )
            );
        }
    }

    public IndexableMapQuerier( IndexableTargetScopeMeta meta, UniformCountSelfLoadingDictCache<V > cache ) {
        this.mManipulator  = (IndexableDataManipulator<K, V >) meta.<K, V >getDataManipulator();
        this.mIndexMeta    = meta;
        this.mCache        = cache;
    }

    public IndexableMapQuerier( IndexableTargetScopeMeta meta, boolean bConcurrent ) {
        this( meta, IndexableMapQuerier.newCache( meta, bConcurrent ) );
    }

    public IndexableMapQuerier( IndexableTargetScopeMeta meta ) {
        this( meta, true );
    }


    @Override
    public long size() {
        return this.mManipulator.counts( this.mIndexMeta, null );
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void clear() {
        this.mCache.clear();
        this.mManipulator.purge( this.mIndexMeta );
    }

    @Override
    public boolean containsKey( Object key ) {
        return this.mCache.implicatesKey( key );
    }

    @Override
    public boolean containsValue( Object value ) {
        Object values = this.mManipulator.selectAllByNS( this.mIndexMeta, null, null );
        if( values instanceof Collection) {
            return ((Collection) values).contains( value );
        }
        else if( values instanceof Map) {
            return ((Map) values).values().contains( value );
        }
        return false;
    }

    @Override
    public V get( Object key ) {
        return this.mCache.get( key );
    }

    @Override
    public V insert( Object key, V value ) {
        this.mManipulator.insert( this.mIndexMeta, (K)key, value );
        return value;
    }

    @Override
    public V insert( Object key, V value, long expireMill ) {
        this.mManipulator.insert( this.mIndexMeta, (K)key, value, expireMill );
        return value;
    }

    @Override
    public V insert( Object key, V value, long expire, TimeUnit unit ) {
        this.insert( key, value, unit.toHours( expire ) );
        return value;
    }

    @Override
    public V insertIfAbsent( Object key, V value ) {
        if ( !this.containsKey( key ) ) {
            return this.insert( key, value );
        }
        return null;
    }

    @Override
    public V insertIfAbsent( Object key, V value, long expireMill ) {
        if ( !this.containsKey( key ) ) {
            return this.insert( key, value, expireMill );
        }
        return null;
    }

    @Override
    public V erase( Object key ) {
        V value = this.get( key );
        this.expunge( key );
        return value;
    }

    @Override
    public void expunge( Object key ) {
        this.mCache.erase( key );
        this.mManipulator.deleteByKey( this.mIndexMeta, key );
    }

    @Override
    public Set<? extends Map.Entry<?, V>> entrySet() {
        Map<K, V> map = this.toMap();
        return map.entrySet();
    }

    @Override
    public Collection<V> values() {
        return this.toMap().values();
    }

    @Override
    public Map<K, V > toMap() {
        if( this.mManipulator instanceof IndexableIterableManipulator ) {
            IndexableIterableManipulator<K, V > manipulator = (IndexableIterableManipulator<K, V >)this.mManipulator;

            return new IndexableCachedMap<>( this.mIndexMeta, this.mCache, this );
        }
        throw new NotImplementedException( "Manipulator should be IterableManipulator." );
    }

    @Override
    public List<V> toList() {
        return new ArrayList<>( this.values() );
    }

}
