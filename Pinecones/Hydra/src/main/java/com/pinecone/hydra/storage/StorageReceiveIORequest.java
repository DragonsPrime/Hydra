package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.util.zip.CRC32;

public interface StorageReceiveIORequest extends Pinenut {
    String getName();
    void setName( String name );

    Number getSize();
    void setSize( Number size );

    GUID getStorageObjectGuid();
    void setStorageObjectGuid( GUID storageObjectGuid );

}
