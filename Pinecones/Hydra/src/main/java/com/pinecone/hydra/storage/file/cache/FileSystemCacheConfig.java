package com.pinecone.hydra.storage.file.cache;

import com.pinecone.framework.system.prototype.Pinenut;

public interface FileSystemCacheConfig extends Pinenut {
    String getRedisHost();

    int getRedisPort();

    int getRedisTimeOut();

    String getRedisPassword();

    int getRedisDatabase();
}
