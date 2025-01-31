package com.pinecone.hydra.storage.file.direct;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.ArchElementNode;

import java.io.File;
import java.net.URI;

public class GenericExternalFolder extends ArchElementNode implements ExternalFolder {
    protected File      mNativeFile;

    protected String    name;

    protected String    parentPath;;

    protected String    path;

    @Override
    public KOMFileSystem parentFileSystem() {
        return null;
    }

    @Override
    public File getNativeFile() {
        return this.mNativeFile;
    }

    @Override
    public URI toURI() {
        return this.mNativeFile.toURI();
    }

    @Override
    public String getParentPath() {
        return this.parentPath;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String[] list() {
        return this.mNativeFile.list();
    }

    @Override
    public File[] listFiles() {
        return this.mNativeFile.listFiles();
    }
}
