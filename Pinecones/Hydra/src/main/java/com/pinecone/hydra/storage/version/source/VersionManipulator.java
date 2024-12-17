package com.pinecone.hydra.storage.version.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface VersionManipulator extends Pinenut {
    void insertObjectVersion(String version, GUID targetStorageObjectGuid, String filePath);

    void removeObjectVersion( String version, String filePath );

    GUID queryObjectGuid( String version, String filePath );
}
