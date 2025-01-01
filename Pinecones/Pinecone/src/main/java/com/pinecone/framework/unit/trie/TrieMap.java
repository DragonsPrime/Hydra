package com.pinecone.framework.unit.trie;

import com.pinecone.framework.system.prototype.PineUnit;

import java.util.Map;

public interface TrieMap<K, V > extends Map<K, V >, PineUnit {

    @Override
    V put( K key, V value );

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

}
