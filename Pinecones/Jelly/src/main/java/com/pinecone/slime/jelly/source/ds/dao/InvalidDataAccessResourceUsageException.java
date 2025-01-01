package com.pinecone.slime.jelly.source.ds.dao;

public class InvalidDataAccessResourceUsageException extends NonTransientDataAccessException {
    public InvalidDataAccessResourceUsageException( String msg ) {
        super(msg);
    }

    public InvalidDataAccessResourceUsageException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}