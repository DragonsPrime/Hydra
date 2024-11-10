package com.pinecone.slime.jelly.source.ds.transaction;

public interface ResourceTransactionManager extends PlatformTransactionManager {
    Object getResourceFactory();
}

