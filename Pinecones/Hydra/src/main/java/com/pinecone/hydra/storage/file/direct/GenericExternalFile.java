package com.pinecone.hydra.storage.file.direct;

import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.ArchElementNode;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class GenericExternalFile extends ArchElementNode implements ExternalFile {
    protected File      mNativeFile;

    protected String    parentPath;;

    protected String    path;

    protected long      physicalSize;

    public GenericExternalFile(File file){
        this.mNativeFile = file;
        this.name = file.getName();
        long lastModified = file.lastModified();
        this.updateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
        this.physicalSize = file.getTotalSpace();
        this.path = file.getPath();
    }


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
        return this.mNativeFile.getPath();
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
