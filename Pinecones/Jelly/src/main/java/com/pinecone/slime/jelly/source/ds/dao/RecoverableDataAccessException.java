package com.pinecone.slime.jelly.source.ds.dao;

public class RecoverableDataAccessException extends DataAccessException {
    public RecoverableDataAccessException( String msg ) {
        super(msg);
    }

    public RecoverableDataAccessException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}