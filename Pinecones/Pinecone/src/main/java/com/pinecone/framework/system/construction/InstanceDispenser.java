package com.pinecone.framework.system.construction;

import com.pinecone.framework.system.prototype.Pinenut;

public interface InstanceDispenser extends Pinenut {
    InstanceDispenser register( Class<?> type ) ;

    boolean  hasRegistered( Class<? > type );

    <T > T allotInstance( Class<T> type ) ;

    void free( Class<?> type, Object instance ) ;

    void free( Object instance );
}
