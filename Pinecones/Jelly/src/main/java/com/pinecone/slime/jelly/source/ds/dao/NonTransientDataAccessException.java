package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.Nullable;

public abstract class NonTransientDataAccessException extends DataAccessException {
    public NonTransientDataAccessException( String msg ) {
        super(msg);
    }

    public NonTransientDataAccessException( @Nullable String msg, @Nullable Throwable cause ) {
        super(msg, cause);
    }
}
