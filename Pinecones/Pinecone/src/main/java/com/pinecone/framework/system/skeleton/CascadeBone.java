package com.pinecone.framework.system.skeleton;

import com.pinecone.framework.util.name.Namespace;
import com.pinecone.framework.util.name.UniNamespace;

public interface CascadeBone extends Bone {
    CascadeBone parent();

    default boolean isRoot() {
        return this.parent() == null;
    }

    default CascadeBone root() {
        CascadeBone p = this;
        CascadeBone c = p;
        while ( p != null ) {
            c = p;
            p = p.parent();
        }

        return c;
    }

    Namespace getName();

    void setName( Namespace name );

    default void setName( String name ) {
        Namespace p = null;
        if( this.parent() != null ) {
            p = this.parent().getName();
        }
        this.setName( new UniNamespace( name, p ) );
    }

    default String getSimpleName() {
        return this.getName().getSimpleName();
    }

    default String getFullName() {
        return this.getName().getFullName();
    }
}
