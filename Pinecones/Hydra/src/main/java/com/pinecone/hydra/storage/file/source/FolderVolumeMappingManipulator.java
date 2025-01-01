package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface FolderVolumeMappingManipulator extends Pinenut {
    void insert( GUID folderGuid, GUID volumeGuid );

    void remove( GUID folderGuid, GUID volumeGuid );

    GUID getVolumeGuid( GUID folderGuid );
}
