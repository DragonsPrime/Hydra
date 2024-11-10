package com.pinecone.slime.jelly.source.ds.jdbc;


import com.pinecone.framework.system.NestedCheckedException;

public class MetaDataAccessException extends NestedCheckedException {
    public MetaDataAccessException( String msg ) {
        super(msg);
    }

    public MetaDataAccessException( String msg, Throwable cause ) {
        super(msg, cause);
    }
}
