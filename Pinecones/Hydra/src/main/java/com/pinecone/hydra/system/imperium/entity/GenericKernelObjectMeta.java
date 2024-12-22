package com.pinecone.hydra.system.imperium.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericKernelObjectMeta implements KernelObjectMeta{
    private long enumId;
    private GUID guid;
    private String name;

    public GenericKernelObjectMeta() {
    }

    public GenericKernelObjectMeta(long enumId, GUID guid, String name) {
        this.enumId = enumId;
        this.guid = guid;
        this.name = name;
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


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "GenericKernelObject{enumId = " + enumId + ", guid = " + guid + ", name = " + name + "}";
    }
}
