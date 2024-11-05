package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;

public class TitanReceiveStorageObject implements ReceiveStorageObject{
    private String name;
    private Number size;
    private GUID   storageObjectGuid;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Number getSize() {
        return this.size;
    }

    @Override
    public void setSize(Number size) {
        this.size = size;
    }

    @Override
    public GUID getStorageObjectGuid() {
        return this.storageObjectGuid;
    }

    @Override
    public void setStorageObjectGuid(GUID storageObjectGuid) {
        this.storageObjectGuid = storageObjectGuid;
    }
}
