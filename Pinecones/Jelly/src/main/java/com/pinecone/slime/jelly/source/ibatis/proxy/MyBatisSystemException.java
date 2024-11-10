package com.pinecone.slime.jelly.source.ibatis.proxy;

import com.pinecone.slime.jelly.source.ds.jdbc.UncategorizedDataAccessException;

public class MyBatisSystemException extends UncategorizedDataAccessException {
    private static final long serialVersionUID = 1284728621670758938L;

    public MyBatisSystemException( Throwable cause ) {
        super((String)null, cause);
    }
}
