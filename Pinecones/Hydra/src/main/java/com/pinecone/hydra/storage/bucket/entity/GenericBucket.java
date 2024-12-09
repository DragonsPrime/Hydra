package com.pinecone.hydra.storage.bucket.entity;

import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public class GenericBucket implements Bucket{
    protected int enumId;

    protected String bucketName;

    protected LocalDateTime createTime;

    protected GUID bucketGuid;

    protected GUID userGuid;

    protected String mountPoint;

    @Override
    public int getEnumId() {
        return this.enumId;
    }

    @Override
    public String getBucketName() {
        return this.bucketName;
    }

    @Override
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public GUID getBucketGuid() {
        return this.bucketGuid;
    }

    @Override
    public void setBucketGuid(GUID bucketGuid) {
        this.bucketGuid = bucketGuid;
    }

    @Override
    public GUID getUserGuid() {
        return this.userGuid;
    }

    @Override
    public void setUserGuid(GUID userGuid) {
        this.userGuid = userGuid;
    }

    @Override
    public String getMountPoint() {
        return this.mountPoint;
    }

    @Override
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
