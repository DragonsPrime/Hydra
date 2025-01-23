package com.pinecone.framework.unit.trie;

import com.pinecone.framework.system.prototype.PineUnit;

import java.util.Map;

public interface TrieMap<K, V > extends Map<K, V >, PineUnit {

    @Override
    V put( K key, V value );

    default Object putEntity( K key, Object value ) {
        return this.putEntity( key, value, false );
    }

    Object putEntity( K key, Object value, boolean isAbsent ) ;

    @Override
    V get( Object key );

    @Override
    boolean containsKey( Object key );

    @Override
    V remove( Object key );

    @Override
    int size();

    @Override
    boolean isEmpty();

    TrieNode<V> queryNode( String path );

    String getSeparator();

    DirectoryNode<V > root();

}
