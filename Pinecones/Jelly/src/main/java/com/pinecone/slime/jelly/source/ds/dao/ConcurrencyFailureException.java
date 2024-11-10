package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.Nullable;

public class ConcurrencyFailureException extends TransientDataAccessException {
    public ConcurrencyFailureException( String msg ) {
        super(msg);
    }

    public ConcurrencyFailureException( String msg, @Nullable Throwable cause ) {
        super(msg, cause);
    }
}