package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.Nullable;

public class NonTransientDataAccessResourceException extends NonTransientDataAccessException {
    public NonTransientDataAccessResourceException( String msg ) {
        super(msg);
    }

    public NonTransientDataAccessResourceException( String msg, @Nullable Throwable cause ) {
        super(msg, cause);
    }
}
