package com.pinecone.hydra.conduct.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericTaskNodeMeta implements TaskNodeMeta{
    private long enumId;
    private GUID guid;

    public GenericTaskNodeMeta() {
    }

    public GenericTaskNodeMeta(long enumId, GUID guid) {
        this.enumId = enumId;
        this.guid = guid;
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

    public String toString() {
        return "GenericTaskNodeMeta{enumId = " + enumId + ", guid = " + guid + "}";
    }
}
