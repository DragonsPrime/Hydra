package com.pinecone.hydra.storage.file.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileSystemAttributes;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.entity.FolderMeta;
import com.pinecone.hydra.storage.file.entity.GenericFolder;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.storage.file.source.FolderManipulator;
import com.pinecone.hydra.storage.file.source.FolderMetaManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

import java.util.ArrayList;
import java.util.List;

public class GenericFolderOperator extends ArchFileSystemOperator{
    private FolderManipulator       folderManipulator;
    private FolderMetaManipulator   folderMetaManipulator;

    public GenericFolderOperator(FileSystemOperatorFactory factory ) {
        this( factory.getMasterManipulator(), factory.getFileSystem() );
        this.factory = factory;
    }

    public GenericFolderOperator(FileMasterManipulator masterManipulator, KOMFileSystem fileSystem ) {
        super( masterManipulator, fileSystem );
        this.folderManipulator      =   masterManipulator.getFolderManipulator();
        this.folderMetaManipulator  =   masterManipulator.getFolderMetaManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        Folder folder  = (Folder) treeNode;
        ImperialTreeNode imperialTreeNode = this.affirmPreinsertionInitialize(treeNode);
        GuidAllocator guidAllocator = this.fileSystem.getGuidAllocator();
        GUID guid = folder.getGuid();

        FileSystemAttributes attributes = folder.getAttributes();
        GUID attributesGuid = guidAllocator.nextGUID();
        if ( attributes != null ){
            attributes.setGuid(attributesGuid);
            this.fileSystemAttributeManipulator.insert(attributes);
        }
        else {
            attributesGuid = null;
        }

        FolderMeta folderMeta = folder.getFolderMeta();
        GUID folderMetaGuid = guidAllocator.nextGUID();
        if ( folderMeta != null ){
            folderMeta.setGuid(folderMetaGuid);
            this.folderMetaManipulator.insert(folderMeta);
        }
        else {
            folderMetaGuid = null;
        }

        imperialTreeNode.setNodeMetadataGUID(folderMetaGuid);
        imperialTreeNode.setBaseDataGUID(attributesGuid);
        this.imperialTree.insert(imperialTreeNode);
        this.folderManipulator.insert(folder);
        return guid;
    }

    @Override
    public void purge(GUID guid) {
        //namespace节点需要递归删除其拥有节点若其引用节点，没有其他引用则进行清理
        List<GUIDImperialTrieNode> childNodes = this.imperialTree.getChildren(guid);
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        if ( !childNodes.isEmpty() ){
            List<GUID > subordinates = this.imperialTree.getSubordinates(guid);
            if ( !subordinates.isEmpty() ){
                for ( GUID subordinateGuid : subordinates ){
                    this.purge( subordinateGuid );
                }
            }
            childNodes = this.imperialTree.getChildren( guid );
            for( GUIDImperialTrieNode childNode : childNodes ){
                List<GUID > parentNodes = this.imperialTree.fetchParentGuids(childNode.getGuid());
                if ( parentNodes.size() > 1 ){
                    this.imperialTree.removeInheritance(childNode.getGuid(),guid);
                }
                else {
                    this.purge( childNode.getGuid() );
                }
            }
        }

        if ( node.getType().getObjectName().equals(GenericFolder.class.getName()) ){
            this.removeNode(guid);
        }
        else {
            UOI uoi = node.getType();
            String metaType = this.getOperatorFactory().getMetaType( uoi.getObjectName() );
            if( metaType == null ) {
                TreeNode newInstance = (TreeNode)uoi.newInstance( new Class<? >[]{ KOMFileSystem.class }, this.fileSystem );
                metaType = newInstance.getMetaType();
            }

            FileSystemOperator operator = this.getOperatorFactory().getOperator( metaType );
            operator.purge( guid );
        }
    }

    @Override
    public FileTreeNode get(GUID guid) {
        return this.getFolderWideData(guid,0);
    }

    @Override
    public FileTreeNode get(GUID guid, int depth) {
        return this.getFolderWideData(guid,depth);
    }

    @Override
    public FileTreeNode getSelf(GUID guid) {
        return this.getFolderWideData(guid,0);
    }

    @Override
    public void update(TreeNode treeNode) {

    }

    @Override
    public void updateName(GUID guid, String name) {

    }

    private Folder getFolderWideData(GUID guid, int depth ){
        Folder fd = this.folderManipulator.getFolderByGuid( guid );
        if ( fd instanceof GenericFolder){
            ((GenericFolder) fd).apply( this.fileSystem );
        }
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);

        if( depth <= 0 ) {
            List<GUIDImperialTrieNode> childNode = this.imperialTree.getChildren(guid);
            ArrayList<GUID> guids = new ArrayList<>();
            for ( GUIDImperialTrieNode n : childNode ){
                guids.add( n.getGuid() );
            }
            ++depth;
            fd.setChildrenGuids( guids, depth );
        }

        FileSystemAttributes attributes = this.fileSystemAttributeManipulator.getAttributes( guid, fd );
        FolderMeta folderMeta = this.folderMetaManipulator.getFolderMetaByGuid( node.getNodeMetadataGUID() );
        fd.setAttributes    ( attributes );
        fd.setFolderMeta ( folderMeta );
        return fd;
    }



    private void removeNode( GUID guid ){
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        this.imperialTree.purge( guid );
        this.imperialTree.removeCachePath(guid);
        this.folderManipulator.remove(guid);
        this.folderMetaManipulator.remove(node.getNodeMetadataGUID());
        //this.fileSystemAttributeManipulator.remove(node.getAttributesGUID());
    }
}
