package com.pinecone.hydra.conduct.entity;

import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public class GenericTaskCommonData implements TaskCommonData{
    private long enumId;
    private GUID guid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public GenericTaskCommonData() {
    }

    public GenericTaskCommonData(long enumId, GUID guid, LocalDateTime createTime, LocalDateTime updateTime) {
        this.enumId = enumId;
        this.guid = guid;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * 获取
     * @return enumId
     */
    public long getEnumId() {
        return enumId;
    }

    /**
     * 设置
     * @param enumId
     */
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    /**
     * 获取
     * @return guid
     */
    public GUID getGuid() {
        return guid;
    }

    /**
     * 设置
     * @param guid
     */
    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    /**
     * 获取
     * @return createTime
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置
     * @param createTime
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取
     * @return updateTime
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置
     * @param updateTime
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String toString() {
        return "GenericTaskCommonData{enumId = " + enumId + ", guid = " + guid + ", createTime = " + createTime + ", updateTime = " + updateTime + "}";
    }
}
