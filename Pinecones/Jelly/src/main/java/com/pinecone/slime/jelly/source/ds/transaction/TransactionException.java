package com.pinecone.slime.jelly.source.ds.transaction;

import com.pinecone.framework.system.NestedRuntimeException;

public abstract class TransactionException extends NestedRuntimeException {
    public TransactionException( String msg ) {
        super(msg);
    }

    public TransactionException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}
