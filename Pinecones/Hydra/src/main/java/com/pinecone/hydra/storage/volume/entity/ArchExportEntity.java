package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.StorageIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract  class ArchExportEntity implements ExporterEntity{
    protected VolumeManager volumeManager;
    protected StorageIORequest storageIORequest;

    public ArchExportEntity(VolumeManager volumeManager, StorageIORequest storageIORequest){
        this.volumeManager = volumeManager;
        this.storageIORequest = storageIORequest;

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
    public StorageIORequest getStorageIORequest() {
        return this.storageIORequest;
    }

    @Override
    public void setStorageIORequest(StorageIORequest storageIORequest) {
        this.storageIORequest = storageIORequest;
    }
}
