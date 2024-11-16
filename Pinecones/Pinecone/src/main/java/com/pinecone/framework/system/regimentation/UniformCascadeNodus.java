package com.pinecone.framework.system.regimentation;

import com.pinecone.framework.util.name.Namespace;

public interface UniformCascadeNodus extends CascadeNodus, UniformNodus {
    @Override
    default Namespace getUniformName() {
        return this.getTargetingName();
    }

    @Override
    default void setUniformName( Namespace name ) {
        this.setTargetingName( name );
    }
}
