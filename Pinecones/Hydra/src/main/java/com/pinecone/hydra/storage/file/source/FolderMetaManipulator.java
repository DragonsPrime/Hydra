package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FolderMeta;

public interface FolderMetaManipulator extends Pinenut {
    FolderMeta getFolderMeta(GUID guid, ElementNode element);
    void insert( FolderMeta folderMeta );
    void remove( GUID guid );
    FolderMeta getFolderMetaByGuid(GUID guid);
}
