package com.pinecone.hydra.storage.file.transmit.receiver;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;

public abstract class ArchReceiveEntity implements ReceiveEntity{
    protected KOMFileSystem fileSystem;
    protected String        destDirPath;
    protected FileNode      file;

    public ArchReceiveEntity( KOMFileSystem fileSystem,String destDirPath,FileNode file ){
        this.fileSystem = fileSystem;
        this.file = file;
        this.destDirPath = destDirPath;
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
}
