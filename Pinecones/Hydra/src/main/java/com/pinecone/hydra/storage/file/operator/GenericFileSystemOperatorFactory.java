package com.pinecone.hydra.storage.file.operator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.GenericFileNode;
import com.pinecone.hydra.storage.file.entity.GenericFolder;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GenericFileSystemOperatorFactory implements FileSystemOperatorFactory{
    protected FileMasterManipulator            fileMasterManipulator;
    @JsonIgnore
    protected KOMFileSystem                    fileSystem;

    protected Map<String, TreeNodeOperator>    registerer = new HashMap<>();

    protected Map<String, String >             metaTypeMap = new TreeMap<>();

    protected void registerDefaultMetaType( Class<?> genericType ) {
        this.metaTypeMap.put( genericType.getName(), genericType.getSimpleName().replace(
                this.fileSystem.getConfig().getVersionSignature(),""
        ));
    }

    protected void registerDefaultMetaTypes() {
        this.registerDefaultMetaType( GenericFolder.class );
        this.registerDefaultMetaType( GenericFileNode.class );
    }

    public GenericFileSystemOperatorFactory( KOMFileSystem fileSystem, FileMasterManipulator fileMasterManipulator ){
        this.fileSystem = fileSystem;
        this.fileMasterManipulator = fileMasterManipulator;

        this.registerer.put(
                DefaultFile,
                new GenericFileOperator( this )
        );

        this.registerer.put(
                DefaultFolder,
                new GenericFolderOperator(this)
        );

        this.registerer.put(
                DefaultExternalSymbolic,
                new GenericExternalSymbolicOperator(this)
        );

        this.registerDefaultMetaTypes();
    }






    @Override
    public void register( String typeName, TreeNodeOperator functionalNodeOperation ) {
        this.registerer.put( typeName, functionalNodeOperation );
    }

    @Override
    public void registerMetaType( Class<?> clazz, String metaType ){
        this.registerMetaType( clazz.getName(), metaType );
    }

    @Override
    public void registerMetaType( String classFullName, String metaType ){
        this.metaTypeMap.put( classFullName, metaType );
    }

    @Override
    public String getMetaType( String classFullName ) {
        return this.metaTypeMap.get( classFullName );
    }

    @Override
    public FileSystemOperator getOperator(String typeName ) {
        //Debug.trace( this.registerer.toString() );
        return (FileSystemOperator) this.registerer.get( typeName );
    }

    @Override
    public KOMFileSystem getFileSystem() {
        return this.fileSystem;
    }

    @Override
    public FileMasterManipulator getMasterManipulator() {
        return this.fileMasterManipulator;
    }
}
