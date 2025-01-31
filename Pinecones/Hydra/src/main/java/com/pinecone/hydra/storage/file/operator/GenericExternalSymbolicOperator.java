package com.pinecone.hydra.storage.file.operator;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.GuidAllocator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;
import com.pinecone.hydra.storage.file.entity.FileMeta;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileSystemAttributes;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.GenericFileNode;
import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class GenericExternalSymbolicOperator extends ArchFileSystemOperator{
    private ExternalSymbolicManipulator externalSymbolicManipulator;

    public GenericExternalSymbolicOperator( FileSystemOperatorFactory factory ) {
        this( factory.getMasterManipulator(), (KOMFileSystem) factory.getFileSystem() );
        this.factory = factory;
    }

    public GenericExternalSymbolicOperator(FileMasterManipulator masterManipulator, KOMFileSystem fileSystem ) {
        super( masterManipulator, fileSystem );
        this.externalSymbolicManipulator = this.fileMasterManipulator.getExternalSymbolicManipulator();
    }
    @Override
    public GUID insert(TreeNode treeNode) {
        ExternalSymbolic externalSymbolic = (ExternalSymbolic) treeNode;
        ImperialTreeNode imperialTreeNode = this.affirmPreinsertionInitialize( treeNode );
        GuidAllocator guidAllocator = this.fileSystem.getGuidAllocator();
        GUID guid = externalSymbolic.getGuid();

        FileSystemAttributes attributes = externalSymbolic.getAttributes();
        GUID attrbutesGuid = guidAllocator.nextGUID();
        if ( attributes != null ){
            attributes.setGuid(attrbutesGuid);
            this.fileSystemAttributeManipulator.insert(attributes);
        }
        else {
            attrbutesGuid = null;
        }

        GUID fileMetaGuid = guidAllocator.nextGUID();

        imperialTreeNode.setBaseDataGUID(attrbutesGuid);
        imperialTreeNode.setNodeMetadataGUID(fileMetaGuid);
        this.imperialTree.insert(imperialTreeNode);
        this.externalSymbolicManipulator.insert( externalSymbolic );

        return guid;
    }

    @Override
    public void purge(GUID guid) {
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        this.imperialTree.purge( guid );
        this.externalSymbolicManipulator.remove( guid );
        this.imperialTree.removeCachePath(guid);
    }

    @Override
    public FileTreeNode get( GUID guid ) {
        FileNode fileTreeNode = this.getFileTreeNodeWideData( guid ).evinceFileNode();
        FileNode thisNode = fileTreeNode;
        while ( true ) {
            GUID affinityGuid = thisNode.getDataAffinityGuid();
            if ( affinityGuid != null ){
                FileNode parent = this.getFileTreeNodeWideData( affinityGuid ).evinceFileNode();
                this.inherit( thisNode, parent );
                thisNode = parent;
            }
            else {
                break;
            }
        }
        return fileTreeNode;
    }

    @Override
    public FileTreeNode get( GUID guid, int depth ) {
        return this.get( guid );
    }

    @Override
    public FileTreeNode getSelf(GUID guid) {
        return this.getFileTreeNodeWideData(guid);
    }

    @Override
    public void update(TreeNode treeNode) {

    }

    @Override
    public void updateName(GUID guid, String name) {

    }

    protected FileTreeNode getFileTreeNodeWideData(GUID guid ){
        GUIDImperialTrieNode node = this.imperialTree.getNode( guid );
        ExternalSymbolic cn = this.externalSymbolicManipulator.getSymbolicByGuid( guid );
        if( cn instanceof GenericFileNode) {
            ((GenericFileNode) cn).apply( this.fileSystem );
        }

        //Notice: Registry attributes is difference from other tree, -- that is, same as DOM;
        //        So in this case, this field is deprecated.
        //Attributes         attributes = this.attributesManipulator.getAttributes( node.getAttributesGUID(), cn );

        FileSystemAttributes attributes = this.fileSystemAttributeManipulator.getAttributes( guid, cn );
        cn.setAttributes    ( attributes );
        return cn;
    }

    protected void inherit( FileTreeNode self, FileTreeNode prototype ){
        Class<? extends FileTreeNode> clazz = self.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for ( Field field : fields ){
            field.setAccessible(true);
            try {
                Object value1 = field.get( self );
                Object value2 = field.get( prototype );
                if ( Objects.isNull(value1) || (value1 instanceof List && ((List<?>) value1).isEmpty()) ){
                    field.set(self,value2);
                }
            }
            catch ( IllegalAccessException e ) {
                throw new ProxyProvokeHandleException(e);
            }
        }
    }
}
