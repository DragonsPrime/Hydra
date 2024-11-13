package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract class ArchReceiveEntity implements ReceiveEntity{
    protected VolumeManager volumeManager;

    protected ReceiveStorageObject receiveStorageObject;


    public ArchReceiveEntity(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject ){
        this.volumeManager = volumeManager;
        this.receiveStorageObject = receiveStorageObject;
    }


    @Override
    public VolumeManager getVolumeManager() {
        return this.volumeManager;
    }

    @Override
    public void setVolumeManager(VolumeManager volumeManager) {
        this.volumeManager = volumeManager;
    }

    @Override
    public ReceiveStorageObject getReceiveStorageObject() {
        return this.receiveStorageObject;
    }

    @Override
    public void setReceiveStorageObject(ReceiveStorageObject receiveStorageObject) {
        this.receiveStorageObject = receiveStorageObject;
    }

}
