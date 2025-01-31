package com.pinecone.hydra.storage.file.direct;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.ArchElementNode;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class GenericExternalFile extends ArchElementNode implements ExternalFile {
    protected File      mNativeFile;

    protected String    name;

    protected String    parentPath;;

    protected String    path;

    @Override
    public KOMFileSystem parentFileSystem() {
        return null;
    }

    @Override
    public Number size() {
        return this.mNativeFile.getTotalSpace();
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
}
