package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Folder extends ElementNode {
    void setNodeAttribute(FileSystemAttributes attributes);

    FolderMeta getFolderMeta();
    void setFolderMeta(FolderMeta folderMeta);

    Map<String, FileTreeNode> getChildren();

    List<GUID > fetchChildrenGuids();

    void setChildrenGuids( List<GUID> contentGuids, int depth );

    List<FileTreeNode > listItem();


    void put ( String key, FileTreeNode val );

    void remove ( String key );

    void put ( ElementNode child );

    Folder createFolder( String name );

    ExternalSymbolic createExternalSymbolic( String name );

    KOMFileSystem getFileTree();

    boolean containsKey  ( String key );


    boolean isEmpty();

    @Override
    default Folder evinceFolder() {
        return this;
    }

    Set<String > keySet();

    Set<Map.Entry<String,FileTreeNode>> entrySet();

    void copyTo(GUID destinationGuid);
    void copyNamespaceMetaTo(GUID destinationGuid);
    long TotalFolderSize();

    void applyVolume( GUID volumeGuid );

    GUID getRelationVolume();

}
