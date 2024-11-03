package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract  class ArchExportEntity implements ExporterEntity{
    protected VolumeManager volumeManager;
    protected ExportStorageObject   exportStorageObject;

    public ArchExportEntity(VolumeManager volumeManager, ExportStorageObject exportStorageObject ){
        this.volumeManager = volumeManager;
        this.exportStorageObject = exportStorageObject;

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
    public ExportStorageObject getExportStorageObject() {
        return this.exportStorageObject;
    }

    @Override
    public void setExportStorageObject(ExportStorageObject exportStorageObject) {
        this.exportStorageObject = exportStorageObject;
    }
}
