package com.pinecone.slime.jelly.source.redis;

import com.pinecone.slime.source.GenericResultConverter;
import com.pinecone.slime.source.indexable.IndexableIterableManipulator;
import com.pinecone.slime.source.indexable.IndexableTargetScopeMeta;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GenericRedisHashManipulator<K extends String, V> implements IndexableIterableManipulator<K, V > {
    private final Jedis mJedis;

    public GenericRedisHashManipulator( Jedis jedis ) {
        this.mJedis           = jedis;
    }

    private String getScopeKey( IndexableTargetScopeMeta meta, Object namespace ) {
        if ( namespace != null && !"".equals( namespace ) ) {
            return namespace.toString();
        }
        else if ( meta.getIndexKey() != null && !meta.getIndexKey().isEmpty() ) {
            return meta.getIndexKey();
        }
        else {
            throw new IllegalArgumentException( "Both namespace and meta's index key are empty." );
        }
    }

    @Override
    public long counts( IndexableTargetScopeMeta meta, String szParentIndexKey ) {
        return this.mJedis.hlen( this.getScopeKey( meta, szParentIndexKey ) );
    }

    @Override
    public long countsByNS( IndexableTargetScopeMeta meta, String szParentIndexKey, Object key) {
        String scopeKey = this.getScopeKey( meta, szParentIndexKey );
        if ( this.mJedis.hexists( scopeKey, key.toString()) ) {
            return 1;
        }
        return 0;
    }

    @Override
    public long countsNS( IndexableTargetScopeMeta meta, String szNamespace ) {
        long fieldCount = 0;
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams().match( szNamespace + "*" ).count( 1000 );

        do {
            ScanResult<Map.Entry<String, String>> scanResult = this.mJedis.hscan( meta.getIndexKey(), cursor, scanParams );
            fieldCount += scanResult.getResult().size();
            cursor = scanResult.getCursor();
        }
        while (!cursor.equals(ScanParams.SCAN_POINTER_START));

        return fieldCount;
    }

    @Override
    public List query( IndexableTargetScopeMeta meta, String szStatement ) {
        throw new UnsupportedOperationException( "Query method not supported for Redis Hash manipulator." );
    }

    @Override
    public List<V> queryVal( IndexableTargetScopeMeta meta, String szStatement ) {
        throw new UnsupportedOperationException( "QueryVal method not supported for Redis Hash manipulator." );
    }

    @Override
    public Object selectAllByNS ( IndexableTargetScopeMeta meta, String szParentIndexKey, Object key ) {
        if( key == null ) {
            String scopeKey = this.getScopeKey( meta, szParentIndexKey );
            return this.mJedis.hgetAll( scopeKey );
        }
        else  {
            return this.selectsByNS( meta, szParentIndexKey, key );
        }
    }

    @Override
    public List<V > selectsByNS( IndexableTargetScopeMeta meta, String szParentIndexKey, Object key ) {
        return List.of( this.selectByNS( meta, szParentIndexKey, key ) );
    }

    @Override
    public V selectByNS( IndexableTargetScopeMeta meta, String szParentIndexKey, Object key ) {
        String scopeKey = this.getScopeKey( meta, szParentIndexKey );
        if( meta.getResultConverter() == null ) {
            meta.setResultConverter( new GenericResultConverter<>( meta.getValueType(), meta.getValueMetaKeys() ));
        }
        Object val = this.mJedis.hget( scopeKey, key.toString() );
        if( val == null ) {
            return null;
        }
        return meta.<V >getResultConverter().convert( val ) ;
    }

    @Override
    public V selectByKey( IndexableTargetScopeMeta meta, Object key ) {
        return this.selectByNS( meta, meta.getIndexKey(), key );
    }

    @Override
    public void insertByNS( IndexableTargetScopeMeta meta, String szParentIndexKey, K key, V entity ) {
        this.mJedis.hset( this.getScopeKey( meta, szParentIndexKey ), key.toString() , entity.toString() );
    }

    @Override
    public void insert( IndexableTargetScopeMeta meta, K key, V entity ) {
        this.insertByNS( meta, meta.getIndexKey(), key, entity );
    }

    @Override
    public void insert( IndexableTargetScopeMeta meta, K key, V entity, long expireMill ) {
        this.insertByNS( meta, meta.getIndexKey(), key, entity ); // Not supported.
    }

    @Override
    public void updateByNS( IndexableTargetScopeMeta meta, String szParentIndexKey, K key, V entity ) {
        this.insertByNS( meta, szParentIndexKey, key, entity );
    }

    @Override
    public void update( IndexableTargetScopeMeta meta, K key, V entity ) {
        this.insert( meta, key, entity );
    }

    @Override
    public void deleteByNS( IndexableTargetScopeMeta meta, String szParentIndexKey, Object key ) {
        this.mJedis.hdel( this.getScopeKey( meta, szParentIndexKey ), key.toString() );
    }

    @Override
    public void deleteByKey( IndexableTargetScopeMeta meta, Object key ) {
        this.deleteByNS( meta, meta.getIndexKey(), key );
    }

    @Override
    public void purge( IndexableTargetScopeMeta meta ) {
        this.purgeByNS( meta, meta.getIndexKey() );
    }

    @Override
    public void purgeByNS( IndexableTargetScopeMeta meta, String szParentIndexKey ) {
        String ns = this.getScopeKey( meta, szParentIndexKey );
        this.mJedis.del( ns );
    }

    @Override
    public void commit() {
        // Redis operations are atomic, no explicit commit needed.
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Iterator<K > keysIterator( IndexableTargetScopeMeta meta ) {
        return (Iterator) new RedisKeysIterator( this.mJedis, "", new IteratorSourceAdapter() {
            @Override
            public ScanResult<Map.Entry<String, String> > scan( String cursor, ScanParams params ) {
                return GenericRedisHashManipulator.this.mJedis.hscan( meta.getIndexKey(), cursor, params );
            }
        });
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Iterator<Map.Entry<K, V > > iterator( IndexableTargetScopeMeta meta ) {
        return (Iterator) new RedisEntryIterator( this.mJedis, "", new IteratorSourceAdapter() {
            @Override
            public ScanResult<Map.Entry<String, String> > scan( String cursor, ScanParams params ) {
                return GenericRedisHashManipulator.this.mJedis.hscan( meta.getIndexKey(), cursor, params );
            }
        });
    }
}
