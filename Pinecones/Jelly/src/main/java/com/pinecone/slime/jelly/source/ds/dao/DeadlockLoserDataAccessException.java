package com.pinecone.slime.jelly.source.ds.dao;

public class DeadlockLoserDataAccessException extends PessimisticLockingFailureException {
    public DeadlockLoserDataAccessException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}

