package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract  class ArchExportEntity implements ExporterEntity{
    protected VolumeManager volumeManager;
    protected StorageExportIORequest storageExportIORequest;

    public ArchExportEntity(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest){
        this.volumeManager = volumeManager;
        this.storageExportIORequest = storageExportIORequest;

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
    public StorageExportIORequest getStorageIORequest() {
        return this.storageExportIORequest;
    }

    @Override
    public void setStorageIORequest(StorageExportIORequest storageExportIORequest) {
        this.storageExportIORequest = storageExportIORequest;
    }
}
