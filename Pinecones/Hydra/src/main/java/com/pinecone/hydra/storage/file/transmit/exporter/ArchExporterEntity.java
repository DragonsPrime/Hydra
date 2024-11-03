package com.pinecone.hydra.storage.file.transmit.exporter;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;

public abstract class ArchExporterEntity implements ExporterEntity{
    private KOMFileSystem fileSystem;
    private FileNode file;


    public ArchExporterEntity(KOMFileSystem fileSystem, FileNode file) {
        this.fileSystem = fileSystem;
        this.file = file;
    }


    public KOMFileSystem getFileSystem() {
        return fileSystem;
    }


    public void setFileSystem(KOMFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    public FileNode getFile() {
        return file;
    }


    public void setFile(FileNode file) {
        this.file = file;
    }

    public String toString() {
        return "ArchExporterEntity{fileSystem = " + fileSystem + ", file = " + file + "}";
    }
}
