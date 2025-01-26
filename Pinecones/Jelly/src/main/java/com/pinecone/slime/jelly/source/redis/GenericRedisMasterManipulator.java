package com.pinecone.slime.jelly.source.redis;

import com.pinecone.framework.system.prototype.ObjectiveBean;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.slime.jelly.source.NamespacedKey;
import com.pinecone.slime.source.GenericResultConverter;
import com.pinecone.slime.source.indexable.IndexableIterableManipulator;
import com.pinecone.slime.source.indexable.IndexableTargetScopeMeta;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

public class GenericRedisMasterManipulator<K extends String, V > implements IndexableIterableManipulator<K, V > {
    private final Jedis  mJedis;
    private final String mszNameSeparator;

    public GenericRedisMasterManipulator( Jedis jedis, String szSeparator ) {
        this.mJedis           = jedis;
        this.mszNameSeparator = szSeparator;
    }

    public GenericRedisMasterManipulator( Jedis jedis ) {
        this( jedis, ":" );
    }

    private String getFullKey( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        return NamespacedKey.getFullKey( meta, this.mszNameSeparator, szNamespace, key );
    }

    private String getKeyType( String key ) {
        try {
            return mJedis.type( key );
        }
        catch ( JedisException e ) {
            // Handle exception (log, throw, etc.)
            return null; // Return null or throw exception to indicate failure
        }
    }

    @Override
    public long counts( IndexableTargetScopeMeta meta, String szScopeKey ) {
        if( szScopeKey == null || szScopeKey.isEmpty() ) {
            return this.mJedis.dbSize();
        }
        try {
            String type = this.getKeyType( szScopeKey );
            if ( "list".equals(type) ) {
                return this.mJedis.llen( szScopeKey );
            }
            else if ( "set".equals(type) ) {
                return this.mJedis.scard( szScopeKey );
            }
            else if ( "zset".equals(type) ) {
                return this.mJedis.zcard( szScopeKey );
            }
            else if ( "hash".equals(type) ) {
                return this.mJedis.hlen( szScopeKey );
            }
            else {
                throw new IllegalArgumentException( "Unsupported data type[ " + type + " ] for counts operation." );
            }
        }
        catch ( JedisException e ) {
            return -1;
        }
    }

    @Override
    public long countsByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        String scopeKey = this.getFullKey( meta, szNamespace, key );
        if( this.mJedis.exists( scopeKey ) ){
            return 1;
        }
        return 0;
    }

    @Override
    public long countsNS( IndexableTargetScopeMeta meta, String szNamespace ) {
        long count = 0;
        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams().match( szNamespace + "*" ).count( 1000 );

        do {
            ScanResult<String > scanResult = this.mJedis.scan( cursor, scanParams );
            count += scanResult.getResult().size();
            cursor = scanResult.getCursor();
        }
        while ( !cursor.equals(ScanParams.SCAN_POINTER_START) );

        return count;
    }

    @Override
    public List query( IndexableTargetScopeMeta meta, String szStatement ) {
        throw new UnsupportedOperationException("Query method not supported for GenericRedisMasterManipulator.");
    }

    @Override
    public List<V> queryVal( IndexableTargetScopeMeta meta, String szStatement ) {
        throw new UnsupportedOperationException("QueryVal method not supported for GenericRedisMasterManipulator.");
    }

    protected Object selectElementByKey( IndexableTargetScopeMeta meta, Object key ) {
        String szKey = key.toString();
        try {
            String type = this.getKeyType( szKey );
            if ( "string".equals( type ) ) {
                String value = this.mJedis.get( szKey );
                if ( value == null ) {
                    return null;
                }
                return value;
            }
            else if ( "hash".equals( type ) ) {
                Map<String, String > map = this.mJedis.hgetAll( szKey );
                if ( map == null ) {
                    return null;
                }
                return map;
            }
            else if ( "list".equals( type ) ) {
                List<String > list = this.mJedis.lrange( szKey, 0, -1 );
                if ( list == null || list.isEmpty() ) {
                    return null;
                }
                return list;
            }
            else if ( "set".equals( type ) ) {
                Set<String > set = this.mJedis.smembers( szKey );
                if ( set == null || set.isEmpty() ) {
                    return null;
                }
                return set;
            }
            else if ( "zset".equals( type ) ) {
                Set<String > zset = this.mJedis.zrange( szKey, 0, -1 );
                if ( zset == null || zset.isEmpty() ) {
                    return null;
                }
                return zset;
            }
            else if ( "none".equals( type ) ) {
                return null;
            }
            else {
                throw new IllegalArgumentException( "Unsupported data type[" + type + "] for selectByNS operation." );
            }
        }
        catch ( JedisException | ClassCastException e ) {
            // Handle exceptions (log, throw, etc.)
            return null;
        }
    }

    @Override
    public Object selectAllByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        if( key != null && szNamespace != null ) {
            return this.selectElementByKey( meta, this.getFullKey( meta, szNamespace, key ) );
        }

        if( szNamespace == null ) {
            szNamespace = "";
        }

        String cursor = ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams().match( szNamespace + "*" ).count( 1000 );

        Map<String, Object > map = new LinkedHashMap<>();
        do {
            ScanResult<String > scanResult = this.mJedis.scan( cursor, scanParams );
            for( String k : scanResult.getResult() ) {
                map.put( k, this.selectElementByKey( meta, this.getFullKey( meta, szNamespace, k ) ) );
            }
            cursor = scanResult.getCursor();
        }
        while ( !cursor.equals(ScanParams.SCAN_POINTER_START) );

        return map;
    }

    @Override
    public List<V > selectsByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        return List.of( this.selectByNS( meta, szNamespace, key ) );
    }

    @Override
    public V selectByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        return selectByKey( meta, this.getFullKey( meta, szNamespace, key ) );
    }

    @Override
    public V selectByKey( IndexableTargetScopeMeta meta, Object key ) {
        if ( meta.getResultConverter() == null ) {
            meta.setResultConverter( new GenericResultConverter<>( meta.getValueType(), meta.getValueMetaKeys() ) );
        }
        return meta.<V >getResultConverter().convert(
                this.selectElementByKey( meta, key )
        );
    }

    protected void insert0( IndexableTargetScopeMeta meta, String szKey, V entity, long expireMill ) {
        try {
            if( entity instanceof String ) {
                this.mJedis.set( szKey, (String)entity );
            }
            else if( entity instanceof Map ) {
                Map<?, ? > map = (Map) entity;
                if( map.get( szKey ) instanceof String ) {
                    this.mJedis.hset( szKey, (Map<String, String>) map ); // Check once.
                }
                else {
                    for( Map.Entry kv : map.entrySet() ) {
                        this.mJedis.hset( szKey, kv.getKey().toString(), kv.getValue().toString() );
                    }
                }
            }
            else if( entity instanceof List ) {
                List<? > list = (List) entity;
                int i = 0;
                for( Object e : list ) {
                    this.mJedis.lset( szKey, i, e.toString() );
                    ++i;
                }
            }
            else if( entity instanceof Set) {
                Set<? > list = (Set) entity;
                for( Object e : list ) {
                    this.mJedis.sadd( szKey, e.toString() );
                }
            }
            else if( entity != null ){
                ObjectiveBean bean = new ObjectiveBean( entity );
                String[] keys = bean.keys();
                for( String k : keys ) {
                    this.mJedis.hset( szKey, k, bean.get( k ).toString() );
                }
            }

            if( expireMill > 0 ) {
                this.mJedis.pexpire( szKey, expireMill );
            }
        }
        catch ( JedisException | ClassCastException e ) {
            // Handle exceptions (log, throw, etc.)
        }
    }

    protected void insert0( IndexableTargetScopeMeta meta, String szKey, V entity ) {
        this.insert0( meta, szKey, entity, -1 );
    }

    @Override
    public void insertByNS( IndexableTargetScopeMeta meta, String szNamespace, K key, V entity ) {
        String scopeKey = this.getFullKey( meta, szNamespace, key );
        this.insert0( meta, scopeKey, entity );
    }

    @Override
    public void insert( IndexableTargetScopeMeta meta, K key, V entity ) {
        this.insert0( meta, key.toString(), entity );
    }

    @Override
    public void insert( IndexableTargetScopeMeta meta, K key, V entity, long expireMill ) {
        this.insert0( meta, key.toString(), entity, expireMill );
    }

    @Override
    public void updateByNS( IndexableTargetScopeMeta meta, String szNamespace, K key, V entity ) {
        this.insertByNS( meta, szNamespace, key, entity );
    }

    @Override
    public void update( IndexableTargetScopeMeta meta, K key, V entity ) {
        this.insert( meta, key, entity );
    }

    @Override
    public void deleteByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        String scopeKey = this.getFullKey( meta, szNamespace,key );
        this.mJedis.unlink( scopeKey );
    }

    @Override
    public void deleteByKey( IndexableTargetScopeMeta meta, Object key ) {
        this.mJedis.unlink( key.toString() );
    }

    @Override
    public void purge( IndexableTargetScopeMeta meta ) {
        this.purgeByNS( meta, meta.getScopeNS() );
    }

    @Override
    public void purgeByNS( IndexableTargetScopeMeta meta, String szNamespace ) {
        this.mJedis.select( Integer.parseInt( szNamespace ) );
        this.mJedis.flushDB();
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
            public ScanResult<String > scan( String cursor, ScanParams params ) {
                return GenericRedisMasterManipulator.this.mJedis.scan( cursor, params );
            }
        });
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Iterator iterator( IndexableTargetScopeMeta meta ) {
        return new EntryIterator( meta );
    }

    protected final class EntryIterator implements Iterator<Map.Entry > {
        Iterator<K > keyIterator;
        IndexableTargetScopeMeta meta;

        EntryIterator( IndexableTargetScopeMeta meta ) {
            this.meta = meta;
            this.keyIterator = GenericRedisMasterManipulator.this.keysIterator( meta );
        }

        @Override
        public final boolean hasNext() {
            return this.keyIterator.hasNext();
        }

        @Override
        public final Map.Entry next() {
            K k = this.keyIterator.next(); // WARNING, Unchecked.
            return new KeyValue<>( k, GenericRedisMasterManipulator.this.selectElementByKey( this.meta, k ) );
        }
    }
}