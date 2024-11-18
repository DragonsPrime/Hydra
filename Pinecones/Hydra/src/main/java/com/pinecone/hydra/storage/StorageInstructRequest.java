package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface StorageInstructRequest extends Pinenut {
    GUID getStorageObjectGuid(); // 存储单位的标识（指针）

    void setStorageObjectGuid( GUID storageObjectGuid );
}
