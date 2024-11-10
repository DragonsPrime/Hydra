package com.pinecone.slime.jelly.source.ds.transaction;

import java.io.Flushable;

public interface TransactionStatus extends TransactionExecution, SavepointManager, Flushable {
    boolean hasSavepoint();

    @Override
    void flush();
}