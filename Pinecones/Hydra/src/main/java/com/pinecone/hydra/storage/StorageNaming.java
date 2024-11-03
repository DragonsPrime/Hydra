package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;

public interface StorageNaming extends Pinenut {
    String naming( String objectName, String crc32 );
}
