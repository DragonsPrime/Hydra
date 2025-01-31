package com.pinecone.hydra.storage.file.entity;

import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;

public interface ExternalSymbolic extends Symbolic {
    void apply( ExternalSymbolicManipulator externalSymbolicManipulator );
}
