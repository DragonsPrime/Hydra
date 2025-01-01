package com.pinecone.slime.jelly.source.ds.dao;

public class CannotAcquireLockException extends PessimisticLockingFailureException {
    public CannotAcquireLockException( String msg ) {
        super(msg);
    }

    public CannotAcquireLockException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}

