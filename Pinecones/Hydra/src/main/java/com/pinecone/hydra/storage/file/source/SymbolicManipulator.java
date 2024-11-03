package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.Symbolic;

public interface SymbolicManipulator extends Pinenut {
    Symbolic getSymbolic(GUID guid, ElementNode element);
    void insert( Symbolic symbolic );
    void remove( GUID guid );
    Symbolic getSymbolicByGuid(GUID guid);
}
