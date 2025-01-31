package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;
import com.pinecone.hydra.storage.file.entity.Symbolic;

public interface ExternalSymbolicManipulator extends Pinenut {
    void insert( ExternalSymbolic externalSymbolic );
    void remove( GUID guid );
    ExternalSymbolic getSymbolicByGuid( GUID guid );

    ExternalSymbolic getSymbolicByNameGuid( String nodeName, GUID nodeGUID );

    boolean isSymbolicMatchedByNameGuid( String nodeName, GUID nodeGUID );
}
