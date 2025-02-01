package com.pinecone.hydra.storage.file.direct;

import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.ArchElementNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class GenericExternalFolder extends ArchElementNode implements ExternalFolder {
    protected File      mNativeFile;
    protected String    parentPath;;

    protected String    path;

    public GenericExternalFolder( File file ){
        this.mNativeFile = file;
        this.name = file.getName();
        long lastModified = file.lastModified();
         this.updateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
         this.path = file.getPath();

    }


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

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public List<FileTreeNode> listItem() {
        ArrayList<FileTreeNode> fileTreeNodes = new ArrayList<>();
        File[] files = this.listFiles();
        if(files.length > 0){
            for( int i = 0;i < files.length; i++ ){
                File file = files[i];
                if(file.isDirectory()){
                    fileTreeNodes.add( new GenericExternalFolder(file) );
                }else {
                    fileTreeNodes.add( new GenericExternalFile( file ) );
                }
            }
        }
        return fileTreeNodes;
    }
}
