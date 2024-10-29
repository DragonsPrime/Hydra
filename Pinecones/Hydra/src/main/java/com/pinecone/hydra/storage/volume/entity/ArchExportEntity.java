package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.volume.VolumeTree;

public abstract  class ArchExportEntity implements ExporterEntity{
    protected VolumeTree            volumeTree;
    protected ExportStorageObject   exportStorageObject;

    public ArchExportEntity( VolumeTree volumeTree, ExportStorageObject exportStorageObject ){
        this.volumeTree = volumeTree;
        this.exportStorageObject = exportStorageObject;

    }
    @Override
    public VolumeTree getVolumeTree() {
        return this.volumeTree;
    }

    @Override
    public void setVolumeTree(VolumeTree volumeTree) {
        this.volumeTree = volumeTree;
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
