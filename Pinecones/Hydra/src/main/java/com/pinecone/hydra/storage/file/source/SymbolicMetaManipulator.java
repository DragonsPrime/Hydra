package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.SymbolicMeta;

public interface SymbolicMetaManipulator extends Pinenut {
    SymbolicMeta getSymbolicMeta(GUID guid, ElementNode element);
    void insert( SymbolicMeta symbolicMeta );
    void remove( GUID guid );
    SymbolicMeta getSymbolicMetaByGuid(GUID guid);
}
