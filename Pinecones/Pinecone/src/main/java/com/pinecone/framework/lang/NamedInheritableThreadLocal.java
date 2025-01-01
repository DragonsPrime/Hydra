package com.pinecone.framework.lang;

import com.pinecone.framework.util.Assert;

public class NamedInheritableThreadLocal<T > extends InheritableThreadLocal<T > {
    private final String name;

    public NamedInheritableThreadLocal( String name ) {
        Assert.hasText( name, "Name must not be empty" );
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
