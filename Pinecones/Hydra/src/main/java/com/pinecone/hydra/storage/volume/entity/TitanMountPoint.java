package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.source.MountPointManipulator;

import java.time.LocalDateTime;

public class TitanMountPoint implements MountPoint{
    protected long                    enumId;
    protected GUID                    guid;
    protected LocalDateTime           createTime;
    protected LocalDateTime           updateTime;
    protected String                  name;
    protected GUID                    volumeGuid;
    protected String                  mountPoint;
    protected VolumeManager volumeManager;
    protected MountPointManipulator   mountPointManipulator;

    public TitanMountPoint(VolumeManager volumeManager, MountPointManipulator mountPointManipulator ){
        this.volumeManager = volumeManager;
        this.mountPointManipulator      =   mountPointManipulator;
        this.guid                       =   volumeManager.getGuidAllocator().nextGUID();
        this.createTime                 =   LocalDateTime.now();
        this.updateTime                 =   LocalDateTime.now();
    }
    public TitanMountPoint(){}


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
    public GUID getVolumeGuid() {
        return this.volumeGuid;
    }

    @Override
    public void setVolumeGuid(GUID volumeGuid) {
        this.volumeGuid = volumeGuid;
    }

    @Override
    public String getMountPoint() {
        return this.mountPoint;
    }

    @Override
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }
    public void setMountPointManipulator( MountPointManipulator mountPointManipulator ){
        this.mountPointManipulator = mountPointManipulator;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
