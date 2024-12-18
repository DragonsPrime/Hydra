package com.pinecone.hydra.storage.version.entity;

import com.pinecone.framework.util.id.GUID;

public interface Version {
    long getEnumId();
    void setEnumId( long enumId );

    String getVersion();
    void setVersion( String version );

    GUID getTargetStorageObjectGuid();
    void setTargetStorageObjectGuid( GUID targetStorageObjectGuid );

    GUID getFileGuid();
    void setFileGuid( GUID fileGuid );

    boolean getEnableCrc32();
    void setEnableCrc32( boolean enableCrc32 );

    String getCrc32();
    void setCrc32( String crc32 );
}
