package com.pinecone.slime.jelly.source.ds.dao;

public class PermissionDeniedDataAccessException extends NonTransientDataAccessException {
    public PermissionDeniedDataAccessException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}