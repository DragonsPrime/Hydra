package com.pinecone.slime.jelly.source.ds.transaction;

import java.io.Flushable;

import com.pinecone.framework.util.comparator.Ordered;

public interface TransactionSynchronization extends Ordered, Flushable {
    int STATUS_COMMITTED = 0;
    int STATUS_ROLLED_BACK = 1;
    int STATUS_UNKNOWN = 2;

    default int getOrder() {
        return 2147483647;
    }

    default void suspend() {
    }

    default void resume() {
    }

    default void flush() {
    }

    default void beforeCommit(boolean readOnly) {
    }

    default void beforeCompletion() {
    }

    default void afterCommit() {
    }

    default void afterCompletion(int status) {
    }
}
