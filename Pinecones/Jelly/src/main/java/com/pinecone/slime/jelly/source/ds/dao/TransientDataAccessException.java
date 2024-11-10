package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.Nullable;

public abstract class TransientDataAccessException extends DataAccessException {
    public TransientDataAccessException( String msg ) {
        super(msg);
    }

    public TransientDataAccessException( String msg, @Nullable Throwable cause ) {
        super(msg, cause);
    }
}

