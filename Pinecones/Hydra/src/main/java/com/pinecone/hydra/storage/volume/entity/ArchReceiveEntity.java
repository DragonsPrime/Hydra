package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract class ArchReceiveEntity implements ReceiveEntity{
    protected VolumeManager volumeManager;

    protected StorageReceiveIORequest storageReceiveIORequest;

    protected Chanface channel;


    public ArchReceiveEntity(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, Chanface channel){
        this.volumeManager = volumeManager;
        this.storageReceiveIORequest = storageReceiveIORequest;
        this.channel = channel;
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
    public StorageReceiveIORequest getReceiveStorageObject() {
        return this.storageReceiveIORequest;
    }

    @Override
    public void setReceiveStorageObject(StorageReceiveIORequest storageReceiveIORequest) {
        this.storageReceiveIORequest = storageReceiveIORequest;
    }

    @Override
    public Chanface getKChannel() {
        return this.channel;
    }

    @Override
    public void setKChannel(Chanface channel) {
        this.channel = channel;
    }
}
