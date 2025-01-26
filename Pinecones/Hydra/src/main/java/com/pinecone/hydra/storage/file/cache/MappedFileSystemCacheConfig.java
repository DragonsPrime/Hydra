package com.pinecone.hydra.storage.file.cache;

import java.util.Map;

public class MappedFileSystemCacheConfig implements FileSystemCacheConfig {
    protected Map<String, Object >  protoConfig;

    protected String redisHost;

    public MappedFileSystemCacheConfig( Map<String, Object > protoConfig ) {
        this.protoConfig = protoConfig;


    }




}
