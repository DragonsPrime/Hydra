package com.pinecone.slime.jelly.source.ds.transaction;

import com.pinecone.framework.system.prototype.Pinenut;

public interface SavepointManager extends Pinenut {
    Object createSavepoint() throws TransactionException;

    void rollbackToSavepoint(Object point) throws TransactionException;

    void releaseSavepoint(Object point) throws TransactionException;
}
