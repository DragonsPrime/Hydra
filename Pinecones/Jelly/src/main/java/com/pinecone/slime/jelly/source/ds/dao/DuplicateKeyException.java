package com.pinecone.slime.jelly.source.ds.dao;

public class DuplicateKeyException extends DataIntegrityViolationException {
    public DuplicateKeyException( String msg ) {
        super(msg);
    }

    public DuplicateKeyException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}
