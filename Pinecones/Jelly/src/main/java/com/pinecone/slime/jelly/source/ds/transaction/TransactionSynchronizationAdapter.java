package com.pinecone.slime.jelly.source.ds.transaction;

import com.pinecone.framework.util.comparator.Ordered;

public abstract class TransactionSynchronizationAdapter implements TransactionSynchronization, Ordered {
    public TransactionSynchronizationAdapter() {
    }

    public int getOrder() {
        return 2147483647;
    }

    public void suspend() {
    }

    public void resume() {
    }

    public void flush() {
    }

    public void beforeCommit(boolean readOnly) {
    }

    public void beforeCompletion() {
    }

    public void afterCommit() {
    }

    public void afterCompletion(int status) {
    }
}
