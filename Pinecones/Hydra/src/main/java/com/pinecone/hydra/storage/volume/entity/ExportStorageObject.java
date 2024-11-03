package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;

public interface ExportStorageObject {
    String getSourceName();
    void setSourceName( String sourceName );

    String getCrc32();
    void setCrc32( String crc32 );

    Number getSize();
    void setSize( Number size );

    GUID getStorageObjectGuid();
    void setStorageObjectGuid(GUID storageObjectGuid );
}
