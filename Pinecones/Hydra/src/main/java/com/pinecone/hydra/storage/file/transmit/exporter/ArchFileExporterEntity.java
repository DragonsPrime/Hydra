package com.pinecone.hydra.storage.file.transmit.exporter;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract class ArchFileExporterEntity implements FileExportEntity {
    protected Chanface channel;

    protected VolumeManager volumeManager;
    private KOMFileSystem fileSystem;
    private FileNode file;


    public ArchFileExporterEntity(KOMFileSystem fileSystem, FileNode file, Chanface channel, VolumeManager volumeManager) {
        this.fileSystem = fileSystem;
        this.file = file;
        this.channel = channel;
        this.volumeManager = volumeManager;
    }

    @Override
    public KOMFileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public void setFileSystem(KOMFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public FileNode getFile() {
        return file;
    }

    @Override
    public void setFile(FileNode file) {
        this.file = file;
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
    public Chanface getKChannel() {
        return this.channel;
    }

    @Override
    public void setKChannel(Chanface channel) {
        this.channel = channel;
    }

    public String toString() {
        return "ArchExporterEntity{fileSystem = " + fileSystem + ", file = " + file + "}";
    }
}
