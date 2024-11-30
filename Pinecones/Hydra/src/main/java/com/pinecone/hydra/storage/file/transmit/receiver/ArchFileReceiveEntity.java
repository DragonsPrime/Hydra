package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeManager;

public abstract class ArchFileReceiveEntity implements FileReceiveEntity {
    protected KOMFileSystem     fileSystem;

    protected String            destDirPath;

    protected FileNode          file;

    protected Chanface channel;

    protected VolumeManager     volumeManager;

    public ArchFileReceiveEntity(KOMFileSystem fileSystem, String destDirPath, FileNode file, Chanface channel, VolumeManager volumeManager ){
        this.fileSystem = fileSystem;
        this.file = file;
        this.destDirPath = destDirPath;
        this.channel  = channel;
        this.volumeManager = volumeManager;
    }

    @Override
    public KOMFileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override
    public void setFileSystem(KOMFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public String getDestDirPath() {
        return this.destDirPath;
    }

    @Override
    public void setDestDirPath(String destDirPath) {
        this.destDirPath = destDirPath;
    }

    @Override
    public FileNode getFile() {
        return this.file;
    }

    @Override
    public void setFile(FileNode file) {
        this.file = file;
    }

    @Override
    public Chanface getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Chanface channel) {
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
}
