package com.pinecone.hydra.storage.file.entity;

public interface Symbolic extends ReparseSemanticNode {
    SymbolicMeta  getSymbolicMeta();

    void setSymbolicMeta( SymbolicMeta symbolicMeta );

    void create();

    void remove();
}
