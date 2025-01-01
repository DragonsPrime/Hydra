package com.pinecone.hydra.storage.bucket.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;

import java.time.LocalDateTime;

public class GenericSite implements Site{
    private long enumId;

    private String siteName;

    private LocalDateTime createTime;

    private GUID siteGuid;

    private GUID mountPointGuid;

    public GenericSite(){}

    public GenericSite(long enumId, String siteName, LocalDateTime createTime, GUID siteGuid, GUID mountPointGuid) {
        this.enumId = enumId;
        this.siteName = siteName;
        this.createTime = createTime;
        this.siteGuid = siteGuid;
        this.mountPointGuid = mountPointGuid;
    }

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public String getSiteName() {
        return this.siteName;
    }

    @Override
    public void setSiteName(String siteName) {
        this.siteName = siteName;
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
    public GUID getSiteGuid() {
        return this.siteGuid;
    }

    @Override
    public void setSiteGuid(GUID siteGuid) {
        this.siteGuid = siteGuid;
    }

    @Override
    public GUID getMountPointGuid() {
        return this.mountPointGuid;
    }

    @Override
    public void setMountPointGuid(GUID mountPointGuid) {
        this.mountPointGuid = mountPointGuid;
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
