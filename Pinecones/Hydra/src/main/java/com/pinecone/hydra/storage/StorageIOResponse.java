package com.pinecone.hydra.storage;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Frame;

import java.util.zip.CRC32;

public interface StorageIOResponse extends StorageInstructResponse {
    GUID getObjectGuid();
    void setObjectGuid( GUID objectGuid );

    GUID getBottomGuid();
    void setBottomGuid( GUID bottomGuid );

    long getChecksum();
    void setChecksum( long checksum );

    long getParityCheck();
    void setParityCheck( long parityCheck );

    CRC32 getCre32();
    void setCrc32( CRC32 crc32 );

    String getSourceName();
    void setSourceName( String name );

    Frame toFrame();
    FileNode toFileNode();

}
