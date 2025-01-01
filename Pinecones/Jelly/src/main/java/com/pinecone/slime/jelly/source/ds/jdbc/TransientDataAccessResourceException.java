package com.pinecone.slime.jelly.source.ds.jdbc;

public class TransientDataAccessResourceException extends TransientDataAccessException {
    public TransientDataAccessResourceException(String msg) {
        super(msg);
    }

    public TransientDataAccessResourceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

