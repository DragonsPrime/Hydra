package com.pinecone.slime.jelly.source.memcached;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.slime.jelly.source.NamespacedKey;
import com.pinecone.slime.source.GenericResultConverter;
import com.pinecone.slime.source.indexable.IndexableTargetScopeMeta;
import net.spy.memcached.MemcachedClient;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GenericMemcachedManipulator<V extends Serializable > implements MemcachedManipulator<String, V > {
    private final MemcachedClient   mMemClient;
    private final String            mszNameSeparator;
    protected int                   mnExpireTime;

    public GenericMemcachedManipulator( MemcachedClient client, String szSeparator, int expire ) {
        this.mMemClient       = client;
        this.mszNameSeparator = szSeparator;
        this.mnExpireTime     = expire;
    }

    public GenericMemcachedManipulator( MemcachedClient client, String szSeparator ) {
        this( client, szSeparator, 0 );
    }

    public GenericMemcachedManipulator( MemcachedClient client ) {
        this( client, ":" );
    }


    @Override
    public MemcachedClient getClient() {
        return this.mMemClient;
    }

    private String getFullKey(IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        return NamespacedKey.getFullKey( meta, this.mszNameSeparator, szNamespace, key );
    }

    @Override
    public long counts( IndexableTargetScopeMeta meta, String szScopeKey ) {
        return this.countsNS( meta, szScopeKey );
    }

    @Override
    public long countsByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        if( this.selectByNS( meta, szNamespace, key ) != null ){
            return 1;
        }
        return 0;
    }

    @Override
    public long countsNS( IndexableTargetScopeMeta meta, String szNamespace ) {
        boolean bEN = StringUtils.isEmpty( szNamespace );

        long count = 0;
        Map<SocketAddress, Map<String, String > > items = this.mMemClient.getStats( "items" );

        for ( Map.Entry<SocketAddress, Map<String, String > > entry : items.entrySet() ) {
            Map<String, String > itemMap = entry.getValue();
            for ( String key : itemMap.keySet() ) {
                if ( key.startsWith("items:") ) {
                    String[] parts = key.split(":");
                    if ( parts.length > 2 && "number".equals(parts[2]) ) {
                        int slabNumber = Integer.parseInt(parts[1]);
                        int limit = Integer.parseInt( itemMap.get(key) );
                        Map<SocketAddress, Map<String, String> > dump = this.mMemClient.getStats( "cachedump " + slabNumber + " " + limit );
                        for ( Map<String, String > dumpMap : dump.values() ) {
                            for( String k : dumpMap.keySet() ){
                                if( bEN || k.startsWith( szNamespace ) ) {
                                    ++count;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public List query( IndexableTargetScopeMeta meta, String szStatement ) {
        throw new UnsupportedOperationException( "Query method not supported for GenericMemcachedManipulator." );
    }

    @Override
    public List<V> queryVal( IndexableTargetScopeMeta meta, String szStatement ) {
        throw new UnsupportedOperationException( "QueryVal method not supported for GenericMemcachedManipulator." );
    }

    @Override
    public Object selectAllByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        if( key != null && szNamespace != null ) {
            return this.mMemClient.get( this.getFullKey( meta, szNamespace, key ) );
        }

        Map<String, Object > map = new LinkedHashMap<>();
        if( szNamespace == null ) {
            Collection<String > keys = this.keys();
            for( String k : keys ) {
                map.put( k, this.mMemClient.get( k ) );
            }
        }
        else {
            Collection<String > keys = this.keys();
            for( String k : keys ) {
                if( k.startsWith( szNamespace ) ) {
                    map.put( k, this.mMemClient.get( k ) );
                }
            }
        }

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
        return meta.<V >getResultConverter().convert( this.mMemClient.get( key.toString() ) );
    }

    protected void insert0( IndexableTargetScopeMeta meta, String szKey, V entity ) {
        Future<Boolean > setFuture = this.mMemClient.set( szKey, this.mnExpireTime, entity );
        try{
            if( !setFuture.get( 5, TimeUnit.SECONDS ) ){
                throw new IllegalStateException( "Unseated key: " + szKey );
            }
        }
        catch ( TimeoutException | ExecutionException | InterruptedException e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }

    @Override
    public void insert( IndexableTargetScopeMeta meta, String key, V entity, long expireMill ) {
        int expireSeconds = (int) (expireMill / 1000);

        Future<Boolean> setFuture = this.mMemClient.set(key, expireSeconds, entity);
        try {
            if ( !setFuture.get( 5, TimeUnit.SECONDS ) ) {
                throw new IllegalStateException("Failed to insert key: " + key);
            }
        }
        catch ( TimeoutException | ExecutionException | InterruptedException e ) {
            throw new ProxyProvokeHandleException(e);
        }
    }

    @Override
    public void insertByNS( IndexableTargetScopeMeta meta, String szNamespace, String key, V entity ) {
        String scopeKey = this.getFullKey( meta, szNamespace, key );
        this.insert0( meta, scopeKey, entity );
    }

    @Override
    public void insert( IndexableTargetScopeMeta meta, String key, V entity ) {
        this.insert0( meta, key.toString(), entity );
    }

    @Override
    public void updateByNS( IndexableTargetScopeMeta meta, String szNamespace, String key, V entity ) {
        this.insertByNS( meta, szNamespace, key, entity );
    }

    @Override
    public void update( IndexableTargetScopeMeta meta, String key, V entity ) {
        this.insert( meta, key, entity );
    }

    @Override
    public void deleteByNS( IndexableTargetScopeMeta meta, String szNamespace, Object key ) {
        String scopeKey = this.getFullKey( meta, szNamespace,key );
        this.deleteByKey( meta, scopeKey );
    }

    @Override
    public void deleteByKey( IndexableTargetScopeMeta meta, Object key ) {
        Future<Boolean > setFuture = this.mMemClient.delete("key1");

        try{
            if( !setFuture.get( 5, TimeUnit.SECONDS ) ){
                throw new IllegalStateException( "Deletion compromised, with key: " + key );
            }
        }
        catch ( TimeoutException | ExecutionException | InterruptedException e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }

    @Override
    public void purge( IndexableTargetScopeMeta meta ) {
        this.purgeByNS( meta, meta.getScopeNS() );
    }

    @Override
    public void purgeByNS( IndexableTargetScopeMeta meta, String szNamespace ) {
        if( szNamespace != null && !szNamespace.isEmpty() ) {
            Collection<String > keys = this.keys();
            for( String k : keys ) {
                if( k.startsWith( szNamespace ) ) {
                    this.deleteByKey( meta, k );
                }
            }
        }
        else {
            this.mMemClient.flush();
        }
    }

    @Override
    public void commit() {
        // Redis operations are atomic, no explicit commit needed.
    }

    @Override
    public Iterator<String > keysIterator( IndexableTargetScopeMeta meta ) {
        return this.keySet().iterator();
    }

    @Override
    public Iterator<Map.Entry<String, V > > iterator( IndexableTargetScopeMeta meta ) {
        return new EntryIterator( meta );
    }

    protected final class EntryIterator implements Iterator<Map.Entry<String, V > > {
        Iterator<String > keyIterator;
        IndexableTargetScopeMeta meta;

        EntryIterator( IndexableTargetScopeMeta meta ) {
            this.meta = meta;
            this.keyIterator = GenericMemcachedManipulator.this.keysIterator( meta );
        }

        @Override
        public final boolean hasNext() {
            return this.keyIterator.hasNext();
        }

        @Override
        public final Map.Entry<String, V > next() {
            String k = this.keyIterator.next();
            return new KeyValue<>( k, GenericMemcachedManipulator.this.selectByKey( this.meta, k ) );
        }
    }
}