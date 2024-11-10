package com.pinecone.slime.jelly.source.ds.dao;

public class PessimisticLockingFailureException extends ConcurrencyFailureException {
    public PessimisticLockingFailureException( String msg ) {
        super(msg);
    }

    public PessimisticLockingFailureException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}

