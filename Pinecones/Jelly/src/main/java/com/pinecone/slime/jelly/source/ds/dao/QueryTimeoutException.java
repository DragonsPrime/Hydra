package com.pinecone.slime.jelly.source.ds.dao;

public class QueryTimeoutException extends TransientDataAccessException {
    public QueryTimeoutException( String msg ) {
        super(msg);
    }

    public QueryTimeoutException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}
