package com.pinecone.hydra.storage.file;

import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.source.indexable.IndexableIterableManipulator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheFileSystemBuilder implements FileSystemBuilder{
    private FileSystemConfig        fileSystemConfig;
    private String                  redisHost;
    private int                     redisPort;
    private int                     redisTimeOut;
    private String                  redisPassword;
    private int                     redisDatabase;

    public CacheFileSystemBuilder(){
        this.fileSystemConfig = new KernelFileSystemConfig();
        this.redisHost = this.fileSystemConfig.getRedisHost();
        this.redisPort = this.fileSystemConfig.getRedisPort();
        this.redisTimeOut = this.fileSystemConfig.getRedisTimeOut();
        this.redisPassword = this.fileSystemConfig.getRedisPassword();
        this.redisDatabase = this.fileSystemConfig.getRedisDatabase();
    }

    KOMFileSystem buildCacheFileSystem(KOIMappingDriver driver ){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool( poolConfig, this.redisHost, this.redisPort, this.redisTimeOut, this.redisPassword, this.redisDatabase );
        Jedis jedis = jedisPool.getResource();
        jedis.auth( this.redisPassword );

        return new UniformObjectFileSystem(driver);
    }
}
