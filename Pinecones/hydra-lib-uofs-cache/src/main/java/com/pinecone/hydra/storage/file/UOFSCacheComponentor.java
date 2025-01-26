package com.pinecone.hydra.storage.file;

import com.pinecone.hydra.storage.file.builder.Feature;
import com.pinecone.hydra.storage.file.builder.UOFSComponentor;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.jelly.source.redis.GenericRedisMasterManipulator;
import com.pinecone.slime.map.indexable.IndexableMapQuerier;
import com.pinecone.slime.source.indexable.GenericIndexableTargetScopeMeta;
import com.pinecone.slime.source.indexable.IndexableIterableManipulator;
import com.pinecone.slime.source.indexable.IndexableTargetScopeMeta;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class UOFSCacheComponentor implements UOFSComponentor {
    private FileSystemConfig        fileSystemConfig;
    private String                  redisHost;
    private int                     redisPort;
    private int                     redisTimeOut;
    private String                  redisPassword;
    private int                     redisDatabase;

    public UOFSCacheComponentor(){
        this.fileSystemConfig = new KernelFileSystemConfig();
        this.redisHost = this.fileSystemConfig.getRedisHost();
        this.redisPort = this.fileSystemConfig.getRedisPort();
        this.redisTimeOut = this.fileSystemConfig.getRedisTimeOut();
        this.redisPassword = this.fileSystemConfig.getRedisPassword();
        this.redisDatabase = this.fileSystemConfig.getRedisDatabase();
    }

    @Override
    public Feature getFeature() {
        return Feature.EnableGlobalCache;
    }

    @Override
    public void apply( KOMFileSystem fs ) {
        UniformObjectFileSystem uofs = (UniformObjectFileSystem) fs;

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool( poolConfig, this.redisHost, this.redisPort, this.redisTimeOut, this.redisPassword, this.redisDatabase );
        Jedis jedis = jedisPool.getResource();
        jedis.auth( this.redisPassword );
        IndexableIterableManipulator<String, String > manipulator = new GenericRedisMasterManipulator<>( jedis );
        IndexableTargetScopeMeta meta = new GenericIndexableTargetScopeMeta( "0", "", Object.class, manipulator );
        IndexableMapQuerier<String, String > querier = new IndexableMapQuerier<>( meta, true );
        uofs.apply( querier );
    }
}
