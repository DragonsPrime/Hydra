package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.NestedRuntimeException;
import com.pinecone.framework.system.Nullable;

public abstract class DataAccessException extends NestedRuntimeException {
    public DataAccessException( String msg ) {
        super(msg);
    }

    public DataAccessException( @Nullable String msg, @Nullable Throwable cause ) {
        super(msg, cause);
    }
}
