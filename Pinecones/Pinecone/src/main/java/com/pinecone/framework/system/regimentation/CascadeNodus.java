package com.pinecone.framework.system.regimentation;

import com.pinecone.framework.util.name.Namespace;
import com.pinecone.framework.util.name.UniNamespace;

public interface CascadeNodus extends Nodus {
    CascadeNodus parent();

    default boolean isRoot() {
        return this.parent() == null;
    }

    default CascadeNodus root() {
        CascadeNodus p = this;
        CascadeNodus c = p;
        while ( p != null ) {
            c = p;
            p = p.parent();
        }

        return c;
    }

    Namespace getTargetingName();

    void setTargetingName( Namespace name );

    default void setTargetingName( String name ) {
        Namespace p = null;
        if( this.parent() != null ) {
            p = this.parent().getTargetingName();
        }
        this.setTargetingName( new UniNamespace( name, p ) );
    }

    default String getSimpleName() {
        return this.getTargetingName().getSimpleName();
    }

    default String getFullName() {
        return this.getTargetingName().getFullName();
    }
}
