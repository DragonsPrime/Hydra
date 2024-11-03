package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;

import java.util.List;

public interface FolderManipulator extends GUIDNameManipulator {
    Folder getFolder(GUID guid, ElementNode element);

    void insert( Folder folder );

    void remove( GUID guid );

    Folder getFolderByGuid(GUID guid);

    List<GUID > getGuidsByName(String name );

    List<GUID > getGuidsByNameID( String name, GUID guid );

    List<GUID > dumpGuid();

    boolean isFolder(GUID guid);
}
