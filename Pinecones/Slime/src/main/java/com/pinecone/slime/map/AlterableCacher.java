package com.pinecone.slime.map;

import java.util.concurrent.TimeUnit;

public interface AlterableCacher<V> extends AlterableQuerier<V > {
    V insert( Object key, V value, long expire, TimeUnit unit ) ;

    V insert( Object key, V value, long expireMill );

    V insertIfAbsent( Object key, V value, long expireMill );
}
