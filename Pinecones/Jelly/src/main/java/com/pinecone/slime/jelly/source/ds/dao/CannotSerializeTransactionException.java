package com.pinecone.slime.jelly.source.ds.dao;

public class CannotSerializeTransactionException extends PessimisticLockingFailureException {
    public CannotSerializeTransactionException( String msg ) {
        super(msg);
    }

    public CannotSerializeTransactionException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}