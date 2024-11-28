package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract  class ArchExportEntity implements ExporterEntity{
    protected VolumeManager volumeManager;

    protected StorageExportIORequest storageExportIORequest;

    protected KChannel  channel;

    public ArchExportEntity(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel){
        this.volumeManager = volumeManager;
        this.storageExportIORequest = storageExportIORequest;
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
    public StorageExportIORequest getStorageIORequest() {
        return this.storageExportIORequest;
    }

    @Override
    public void setStorageIORequest(StorageExportIORequest storageExportIORequest) {
        this.storageExportIORequest = storageExportIORequest;
    }

    @Override
    public KChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(KChannel channel) {
        this.channel = channel;
    }
}
