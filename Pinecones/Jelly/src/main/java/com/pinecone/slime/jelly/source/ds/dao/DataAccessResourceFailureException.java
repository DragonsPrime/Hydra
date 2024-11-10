package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.Nullable;

public class DataAccessResourceFailureException extends NonTransientDataAccessResourceException {
    public DataAccessResourceFailureException(String msg) {
        super(msg);
    }

    public DataAccessResourceFailureException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

