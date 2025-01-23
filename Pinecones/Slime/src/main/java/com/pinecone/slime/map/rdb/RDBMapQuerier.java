package com.pinecone.slime.map.rdb;

import com.pinecone.framework.system.NotImplementedException;
import com.pinecone.slime.cache.CacheConstants;
import com.pinecone.slime.cache.query.SourceRetriever;
import com.pinecone.slime.cache.query.UniformCountSelfLoadingDictCache;
import com.pinecone.slime.cache.query.pool.BatchPageSourceRetriever;
import com.pinecone.slime.cache.query.pool.CountSelfPooledPageDictCache;
import com.pinecone.slime.cache.query.pool.LocalLRUPrimaryPooledDictCache;
import com.pinecone.slime.map.AlterableQuerier;
import com.pinecone.slime.map.MonoKeyQueryRange;
import com.pinecone.slime.source.rdb.ContiguousNumIndexBatchPageSourceRetriever;
import com.pinecone.slime.source.rdb.RDBQuerierDataManipulator;
import com.pinecone.slime.source.rdb.RDBTargetTableMeta;
import com.pinecone.slime.source.rdb.RangedRDBQuerierDataManipulator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RDBMapQuerier<K, V > implements AlterableQuerier<V > {
    private final RDBQuerierDataManipulator<K, V >       mDataMapper;
    protected UniformCountSelfLoadingDictCache<V >       mCache;
    protected RDBTargetTableMeta                         mTableMeta;

    public RDBMapQuerier( RDBTargetTableMeta tableMeta, UniformCountSelfLoadingDictCache<V > cache ) {
        this.mDataMapper = (RDBQuerierDataManipulator<K, V >) tableMeta.<K, V >getDataManipulator();
        this.mTableMeta  = tableMeta;
        this.mCache      = cache;
    }

    public RDBMapQuerier( RDBTargetTableMeta tableMeta, String szRangeKey ) {
        this( tableMeta, new LocalLRUPrimaryPooledDictCache<>( CacheConstants.DefaultCachePageCapacity, 5,
                new ContiguousNumIndexBatchPageSourceRetriever<>( tableMeta, CacheConstants.DefaultCachePageCapacity, szRangeKey )
        ) );
    }

    public RDBMapQuerier( RDBTargetTableMeta tableMeta ) {
        this( tableMeta, tableMeta.getPrimaryKey() );
    }

    @Override
    public long size() {
        return this.mDataMapper.counts( this.mTableMeta,"" );
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void clear() {
        this.mCache.clear();
        this.mDataMapper.truncate( this.mTableMeta );
    }

    @Override
    public boolean containsKey( Object key ) {
        return this.mCache.implicatesKey( key );
    }

    @Override
    public boolean containsValue( Object value ) {
        throw new NotImplementedException( "Querier::containsValue is not implemented." );
        //return this.dataMapper.selectListByColumn( this.tableMeta,  )
    }

    @Override
    public V get( Object key ) {
        return this.mCache.get( key );
    }

    @Override
    public List<V > queryVal( Object statement ) {
        if( statement instanceof String ) {
            return this.mDataMapper.queryVal( this.mTableMeta, (String) statement );
        }
        return this.mDataMapper.queryVal( this.mTableMeta, statement.toString() );
    }

    public List<? > query( Object statement ) {
        if( statement instanceof String ) {
            return this.mDataMapper.query( this.mTableMeta, (String) statement );
        }
        return this.mDataMapper.query( this.mTableMeta, statement.toString() );
    }

    @Override
    public V insert( Object key, V value ) {
        try{
            this.mDataMapper.insert( this.mTableMeta, (K)key, value );
        }
        catch ( Exception e ) {
            this.mDataMapper.update( this.mTableMeta, (K)key, value );
            if( this.mCache.existsKey( key ) ) {
                this.mCache.erase( key );
            }
        }
        return value;
    }

    @Override
    public V insertIfAbsent( Object key, V value ) {
        if( !this.containsKey( key ) ) {
            return this.insert( key, value );
        }
        return null;
    }

    @Override
    public V erase( Object key ) {
        V v = this.get( key );
        this.expunge( key );
        return v;
    }

    @Override
    public void expunge( Object key ) {
        this.mCache.erase( key );
        this.mDataMapper.deleteByKey( this.mTableMeta, key );
    }

    @Override
    public Set<? extends Map.Entry<?, V > > entrySet() {
        return this.toMap().entrySet();
    }

    @Override
    public Collection<V > values() {
        try{
            Map<?, V > map = this.toMap();
            return map.values();
        }
        catch ( NotImplementedException e ) {
            return this.toList();
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Map<?, V > toMap() {
        if( this.mDataMapper instanceof RangedRDBQuerierDataManipulator ) {
            RangedRDBQuerierDataManipulator<K, V > manipulator = (RangedRDBQuerierDataManipulator<K, V >)this.mDataMapper;
            SourceRetriever<V > retriever = this.mCache.getSourceRetriever();
            String szRangeKey = this.mTableMeta.getIndexKey();


            if( this.mCache instanceof CountSelfPooledPageDictCache && retriever instanceof BatchPageSourceRetriever ) {
                return new RangedRDBCachedMap<>( this.mTableMeta, (CountSelfPooledPageDictCache)this.mCache, this );
            }
            else {
                if( retriever instanceof BatchPageSourceRetriever ) {
                    szRangeKey = ((BatchPageSourceRetriever<V >) retriever).getRangeKey();
                }
                Object max = manipulator.getMaximumRangeVal( this.mTableMeta, szRangeKey );
                Object min = manipulator.getMinimumRangeVal( this.mTableMeta, szRangeKey );

                if( max instanceof Comparable ) {
                    return (Map<?, V >)manipulator.selectMappedByRange( this.mTableMeta, new MonoKeyQueryRange<>( (Comparable)min, (Comparable)max, szRangeKey ) );
                }
            }
        }
        throw new NotImplementedException( "Manipulator should be has `Ranged`, and max/min should be `Comparable`." );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<V > toList() {
        return ( List<V > ) this.mDataMapper.selectList( this.mTableMeta, "" );
    }
}