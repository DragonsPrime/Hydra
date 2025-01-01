package com.pinecone.slime.jelly.source.ds.transaction;

public class TransactionTimedOutException extends TransactionException {
    public TransactionTimedOutException( String msg ) {
        super(msg);
    }

    public TransactionTimedOutException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}