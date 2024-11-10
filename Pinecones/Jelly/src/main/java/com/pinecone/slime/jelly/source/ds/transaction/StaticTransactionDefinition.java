package com.pinecone.slime.jelly.source.ds.transaction;

final class StaticTransactionDefinition implements TransactionDefinition {
    static final StaticTransactionDefinition INSTANCE = new StaticTransactionDefinition();

    private StaticTransactionDefinition() {
    }
}
