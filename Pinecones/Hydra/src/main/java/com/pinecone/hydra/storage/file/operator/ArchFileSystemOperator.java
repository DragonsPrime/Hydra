package com.pinecone.hydra.storage.file.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.ArchElementNode;
import com.pinecone.hydra.storage.file.source.FileSystemAttributeManipulator;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;

import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

import java.time.LocalDateTime;

public abstract class ArchFileSystemOperator implements FileSystemOperator{
    protected KOMFileSystem                     fileSystem;
    protected FileSystemOperatorFactory         factory;
    protected ImperialTree                      imperialTree;
    protected FileSystemAttributeManipulator    fileSystemAttributeManipulator;
    protected FileMasterManipulator             fileMasterManipulator;

    public ArchFileSystemOperator( FileSystemOperatorFactory factory ) {
        this( factory.getMasterManipulator(), (KOMFileSystem) factory.getFileSystem() );
        this.factory = factory;
    }

    public ArchFileSystemOperator( FileMasterManipulator masterManipulator, KOMFileSystem fileSystem ) {
        this.imperialTree =  fileSystem.getMasterTrieTree();
        this.fileSystemAttributeManipulator =  masterManipulator.getAttributeManipulator();
        this.fileSystem                     =  fileSystem;
        this.fileMasterManipulator          =  masterManipulator;
    }

    protected ImperialTreeNode affirmPreinsertionInitialize(TreeNode treeNode ) {
        ArchElementNode entityNode   = (ArchElementNode) treeNode;

        GUID guid72 = entityNode.getGuid();
        // Case 1: Dummy config node.
        GuidAllocator guidAllocator = this.fileSystem.getGuidAllocator();
        if( guid72 == null ) {
            guid72 = guidAllocator.nextGUID();
            entityNode.setGuid( guid72 );
            entityNode.setCreateTime( LocalDateTime.now() );
        }
        entityNode.setUpdateTime( LocalDateTime.now() );

        ImperialTreeNode imperialTreeNode = new GUIDImperialTrieNode();
        imperialTreeNode.setGuid( guid72 );
        imperialTreeNode.setType( UOIUtils.createLocalJavaClass( entityNode.getClass().getName() ) );

        return imperialTreeNode;
    }

    public FileSystemOperatorFactory getOperatorFactory() {
        return this.factory;
    }
}
