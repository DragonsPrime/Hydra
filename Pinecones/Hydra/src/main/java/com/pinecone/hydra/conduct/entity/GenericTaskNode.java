package com.pinecone.hydra.conduct.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericTaskNode implements TaskNode{
    private long enumId;
    private GUID guid;
    private String name;
    private GenericTaskCommonData genericTaskCommonData;
    private GenericTaskNodeMeta genericTaskNodeMeta;

    public GenericTaskNode() {
    }

    public GenericTaskNode(long enumId, GUID guid, String name, GenericTaskCommonData genericTaskCommonData, GenericTaskNodeMeta genericTaskNodeMeta) {
        this.enumId = enumId;
        this.guid = guid;
        this.name = name;
        this.genericTaskCommonData = genericTaskCommonData;
        this.genericTaskNodeMeta = genericTaskNodeMeta;
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
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     * @return genericTaskCommonData
     */
    public GenericTaskCommonData getGenericTaskCommonData() {
        return genericTaskCommonData;
    }

    /**
     * 设置
     * @param genericTaskCommonData
     */
    public void setGenericTaskCommonData(GenericTaskCommonData genericTaskCommonData) {
        this.genericTaskCommonData = genericTaskCommonData;
    }

    /**
     * 获取
     * @return genericTaskNodeMeta
     */
    public GenericTaskNodeMeta getGenericTaskNodeMeta() {
        return genericTaskNodeMeta;
    }

    /**
     * 设置
     * @param genericTaskNodeMeta
     */
    public void setGenericTaskNodeMeta(GenericTaskNodeMeta genericTaskNodeMeta) {
        this.genericTaskNodeMeta = genericTaskNodeMeta;
    }

    public String toString() {
        return "GenericTaskNode{enumId = " + enumId + ", guid = " + guid + ", name = " + name + ", genericTaskCommonData = " + genericTaskCommonData + ", genericTaskNodeMeta = " + genericTaskNodeMeta + "}";
    }
}
