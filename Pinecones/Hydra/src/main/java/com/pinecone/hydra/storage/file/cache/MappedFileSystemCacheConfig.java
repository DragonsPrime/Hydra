package com.pinecone.hydra.storage.file.cache;

import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;

import java.util.Map;

public class MappedFileSystemCacheConfig implements FileSystemCacheConfig {
    //protected Map<String, Object >  protoConfig;

    protected JSONObject            protoConfig;

    protected String                redisHost;

    protected int                   redisPort;

    protected int                   redisTimeOut;

    protected String                redisPassword;

    protected int                   redisDatabase;

    public MappedFileSystemCacheConfig( JSONObject protoConfig ){
        this.protoConfig = protoConfig;
        this.redisHost = this.protoConfig.optString("redisHost");
        this.redisPort = this.protoConfig.optInt("redisPort", 6379);
        this.redisTimeOut = this.protoConfig.optInt("redisTimeOut",2000);
        this.redisPassword = this.protoConfig.optString("redisPassword");
        this.redisDatabase = this.protoConfig.optInt( "redisDatabase" );
    }


    @Override
    public String getRedisHost() {
        return this.redisHost;
    }

    @Override
    public int getRedisPort() {
        return this.redisPort;
    }

    @Override
    public int getRedisTimeOut() {
        return this.redisTimeOut;
    }

    @Override
    public String getRedisPassword() {
        return this.redisPassword;
    }

    @Override
    public int getRedisDatabase() {
        return this.redisDatabase;
    }
}
