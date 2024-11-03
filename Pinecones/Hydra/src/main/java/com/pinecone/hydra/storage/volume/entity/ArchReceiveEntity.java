package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract class ArchReceiveEntity implements ReceiveEntity{
    protected VolumeManager volumeManager;

    protected ReceiveStorageObject receiveStorageObject;

    protected String     destDirPath;

    public ArchReceiveEntity(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, String destDirPath ){
        this.volumeManager = volumeManager;
        this.receiveStorageObject = receiveStorageObject;
        this.destDirPath = destDirPath;
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

    @Override
    public String getDestDirPath() {
        return this.destDirPath;
    }

    @Override
    public void setDestDirPath(String destDirPath) {
        this.destDirPath = destDirPath;
    }
}
