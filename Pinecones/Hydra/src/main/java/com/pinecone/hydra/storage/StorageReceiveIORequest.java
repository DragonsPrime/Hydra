package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface StorageReceiveIORequest extends Pinenut {
    String getName();
    void setName( String name );

    Number getSize();
    void setSize( Number size );

    GUID getStorageObjectGuid();
    void setStorageObjectGuid( GUID storageObjectGuid );
}
