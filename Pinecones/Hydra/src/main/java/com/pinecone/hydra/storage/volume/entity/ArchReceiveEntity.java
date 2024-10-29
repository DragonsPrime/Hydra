package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeFile;
import com.pinecone.hydra.storage.volume.VolumeTree;

public abstract class ArchReceiveEntity implements ReceiveEntity{
    protected VolumeTree volumeTree;
    protected ReceiveStorageObject receiveStorageObject;

    protected String     destDirPath;

    public ArchReceiveEntity( VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, String destDirPath ){
        this.volumeTree = volumeTree;
        this.receiveStorageObject = receiveStorageObject;
        this.destDirPath = destDirPath;
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
