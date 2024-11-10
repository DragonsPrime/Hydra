package com.pinecone.slime.jelly.source.ds.dao;

public class DataIntegrityViolationException extends NonTransientDataAccessException {
    public DataIntegrityViolationException( String msg ) {
        super(msg);
    }

    public DataIntegrityViolationException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}
