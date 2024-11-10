package com.pinecone.slime.jelly.source.ds.transaction;

import com.pinecone.framework.system.prototype.Pinenut;

public interface TransactionExecution extends Pinenut {
    boolean isNewTransaction();

    void setRollbackOnly();

    boolean isRollbackOnly();

    boolean isCompleted();
}
