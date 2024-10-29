package com.pinecone.hydra.storage.volume.entity;

public class TitanReceiveStorageObject implements ReceiveStorageObject{
    private String name;
    private Number size;

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
}
