package com.pinecone.hydra.system.ko;

import com.pinecone.framework.system.skeleton.CascadeBone;
import com.pinecone.framework.util.name.Namespace;

public interface CascadeInstrument extends KernelObjectInstrument, CascadeBone {
    @Override
    CascadeInstrument parent();

    void setParent( CascadeInstrument parent );

    @Override
    default boolean isRoot() {
        return this.parent() == null;
    }

    default CascadeInstrument root() {
        return (CascadeInstrument) CascadeBone.super.root();
    }

    @Override
    Namespace getName();

    @Override
    void setName( Namespace name );

    @Override
    default void setName( String name ) {
        CascadeBone.super.setName( name );
    }

    @Override
    default String getSimpleName() {
        return this.getName().getSimpleName();
    }

    @Override
    default String getFullName() {
        return this.getName().getFullName();
    }
}
