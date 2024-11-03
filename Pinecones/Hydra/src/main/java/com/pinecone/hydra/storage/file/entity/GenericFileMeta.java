package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;

public class GenericFileMeta implements FileMeta {
    private long enumId;
    private GUID guid;

    public GenericFileMeta() {
    }

    public GenericFileMeta(long enumId, GUID guid) {
        this.enumId = enumId;
        this.guid = guid;
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

    public String toString() {
        return "GenericFileMeta{enumId = " + enumId + ", guid = " + guid + "}";
    }
}
