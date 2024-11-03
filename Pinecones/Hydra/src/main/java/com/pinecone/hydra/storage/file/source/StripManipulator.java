package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.FileMeta;
import com.pinecone.hydra.storage.file.entity.Strip;

public interface StripManipulator extends Pinenut {
    void insert( Strip strip );
    void remove( GUID guid );
    Strip getStrip(GUID guid);
}
