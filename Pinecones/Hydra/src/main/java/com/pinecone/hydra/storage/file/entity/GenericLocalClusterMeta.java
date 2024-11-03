package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.id.GUID;

import java.util.Map;

public class GenericLocalClusterMeta implements LocalClusterMeta{
    private long                                enumId;
    private GUID                                guid;
    private String                              key;
    private String                              value;
    protected Map<String, String > metas = new LinkedTreeMap<>();


    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
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
    public String getKey() {
        return this.key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
