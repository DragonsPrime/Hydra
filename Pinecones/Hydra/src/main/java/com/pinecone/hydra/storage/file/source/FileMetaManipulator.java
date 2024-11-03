package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FileMeta;

public interface FileMetaManipulator extends Pinenut {
    FileMeta getFileMeta(GUID guid, ElementNode element);
    void insert( FileMeta fileMeta );
    void remove( GUID guid );
    FileMeta getFileMetaByGuid(GUID guid);
}
