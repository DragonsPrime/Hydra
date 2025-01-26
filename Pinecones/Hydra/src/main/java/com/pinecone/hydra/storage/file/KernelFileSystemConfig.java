package com.pinecone.hydra.storage.file;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.StorageConstants;
import com.pinecone.hydra.storage.file.cache.DefaultCacheConstants;
import com.pinecone.hydra.storage.volume.VolumeConstants;
import com.pinecone.hydra.system.ko.ArchKernelObjectConfig;

public class KernelFileSystemConfig extends ArchKernelObjectConfig implements FileSystemConfig {
    protected String mszVersionSignature    = StorageConstants.StorageVersionSignature;
    protected Number mnFrameSize            = FileConstants.DefaultFrameSize;
    protected GUID   mLocalhostGUID         = StorageConstants.LocalhostGUID;
    protected Number TinyFileStripSizing    = VolumeConstants.TinyFileStripSizing;
    protected String DefaultVolumePath      = StorageConstants.DefaultVolumePath;
    protected long    DefaultExpiryTime      = DefaultCacheConstants.PathQueryExpiryTimeHotMil;
    protected String RedisHost              = FileConstants.REDIS_HOST;
    protected int    RedisPort              = FileConstants.REDIS_PORT;
    protected int    RedisTimeOut           = FileConstants.REDIS_TIME_OUT;
    protected String RedisPassword          = FileConstants.REDIS_PASSWORD;
    protected int    RedisDatabase          = FileConstants.REDIS_DATABASE;


    @Override
    public String getVersionSignature() {
        return this.mszVersionSignature;
    }

    public Number getFrameSize() {
        return this.mnFrameSize;
    }

    public GUID getLocalhostGUID() {
        return this.mLocalhostGUID;
    }

    @Override
    public Number getTinyFileStripSizing() {
        return this.TinyFileStripSizing;
    }

    @Override
    public String getDefaultVolume() {
        return this.DefaultVolumePath;
    }

    @Override
    public long getExpiryTime() {
        return this.DefaultExpiryTime;
    }

    @Override
    public String getRedisHost() {
        return this.RedisHost;
    }

    @Override
    public int getRedisPort() {
        return this.RedisPort;
    }

    @Override
    public int getRedisTimeOut() {
        return this.RedisTimeOut;
    }

    @Override
    public String getRedisPassword() {
        return this.RedisPassword;
    }

    @Override
    public int getRedisDatabase() {
        return this.RedisDatabase;
    }
}
