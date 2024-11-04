package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;

import java.time.LocalDateTime;

public abstract class ArchVolume implements Volume{
    protected long                    enumId;
    protected GUID                    guid;
    protected LocalDateTime           createTime;
    protected LocalDateTime           updateTime;
    protected String                  name;
    protected String                  type;
    protected String                  extConfig;
    protected VolumeManager           volumeManager;
    protected VolumeCapacity64        volumeCapacity;
    protected OnVolumeFileSystem      kenVolumeFileSystem;

    public ArchVolume( VolumeManager volumeManager){
        this.volumeManager = volumeManager;
        this.guid = volumeManager.getGuidAllocator().nextGUID72();
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this. kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    public ArchVolume(){}

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public VolumeCapacity64 getVolumeCapacity() {
        return this.volumeCapacity;
    }

    @Override
    public void setVolumeCapacity(VolumeCapacity64 volumeCapacity) {
        this.volumeCapacity = volumeCapacity;
    }

    @Override
    public String getExtConfig() {
        return this.extConfig;
    }

    @Override
    public void setExtConfig(String extConfig) {
        this.extConfig = extConfig;
    }
}
