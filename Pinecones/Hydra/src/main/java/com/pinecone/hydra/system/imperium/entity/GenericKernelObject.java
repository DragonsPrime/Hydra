package com.pinecone.hydra.system.imperium.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericKernelObject implements KernelObject{
    private long enumId;
    private GUID guid;
    private GUID targetGuid;
    private String targetType;
    private String targetSimpleType;

    public GenericKernelObject() {
    }

    public GenericKernelObject(long enumId, GUID guid, GUID targetGuid, String targetType, String targetSimpleType) {
        this.enumId = enumId;
        this.guid = guid;
        this.targetGuid = targetGuid;
        this.targetType = targetType;
        this.targetSimpleType = targetSimpleType;
    }


    public long getEnumId() {
        return enumId;
    }


    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }


    public GUID getGuid() {
        return guid;
    }


    public void setGuid(GUID guid) {
        this.guid = guid;
    }


    public GUID getTargetGuid() {
        return targetGuid;
    }


    public void setTargetGuid(GUID targetGuid) {
        this.targetGuid = targetGuid;
    }


    public String getTargetType() {
        return targetType;
    }


    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }


    public String getTargetSimpleType() {
        return targetSimpleType;
    }


    public void setTargetSimpleType(String targetSimpleType) {
        this.targetSimpleType = targetSimpleType;
    }

    public String toString() {
        return "GenericKernelObject{enumId = " + enumId + ", guid = " + guid + ", targetGuid = " + targetGuid + ", targetType = " + targetType + ", targetSimpleType = " + targetSimpleType + "}";
    }
}
