package com.pinecone.slime.jelly.source.ds.transaction;

import com.pinecone.framework.system.Nullable;

public interface PlatformTransactionManager extends TransactionManager {
    TransactionStatus getTransaction( @Nullable TransactionDefinition definition ) throws TransactionException;

    void commit( TransactionStatus status ) throws TransactionException;

    void rollback( TransactionStatus status ) throws TransactionException;
}
