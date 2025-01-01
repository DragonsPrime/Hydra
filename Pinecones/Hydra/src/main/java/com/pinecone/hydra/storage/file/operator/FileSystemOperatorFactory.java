package com.pinecone.hydra.storage.file.operator;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.unit.imperium.operator.OperatorFactory;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;

public interface FileSystemOperatorFactory extends OperatorFactory {
    String DefaultFile          =   FileNode.class.getSimpleName();
    String DefaultFolder        =   Folder.class.getSimpleName();

    void register( String typeName, TreeNodeOperator functionalNodeOperation );

    void registerMetaType( Class<?> clazz, String metaType );

    void registerMetaType( String classFullName, String metaType );

    String getMetaType( String classFullName );

    FileSystemOperator getOperator(String typeName );

    KOMFileSystem getFileSystem();

    FileMasterManipulator getMasterManipulator();
}
