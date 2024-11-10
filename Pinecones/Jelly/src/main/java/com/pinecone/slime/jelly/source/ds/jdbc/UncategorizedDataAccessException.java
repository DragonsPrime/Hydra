package com.pinecone.slime.jelly.source.ds.jdbc;

import com.pinecone.framework.system.Nullable;
import com.pinecone.slime.jelly.source.ds.dao.NonTransientDataAccessException;

public abstract class UncategorizedDataAccessException extends NonTransientDataAccessException {
    public UncategorizedDataAccessException( @Nullable String msg, @Nullable Throwable cause ) {
        super( msg, cause );
    }
}

