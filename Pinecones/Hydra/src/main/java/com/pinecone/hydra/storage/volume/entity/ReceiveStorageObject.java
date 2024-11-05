package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface ReceiveStorageObject extends Pinenut {
    String getName();
    void setName( String name );

    Number getSize();
    void setSize( Number size );

    GUID getStorageObjectGuid();
    void setStorageObjectGuid( GUID storageObjectGuid );
}
