package com.pinecone.framework.lang;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.Assert;

public class NamedThreadLocal<T> extends ThreadLocal<T> implements Pinenut {
    private final String name;

    public NamedThreadLocal( String name ) {
        Assert.hasText(name, "Name must not be empty");
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
