package com.pinecone.slime.map;

public interface AlterableCacher<V> extends AlterableQuerier<V > {
    V insert( Object key, V value, long expireMill );

    V insertIfAbsent( Object key, V value, long expireMill );
}
