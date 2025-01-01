package com.pinecone.hydra.unit.imperium.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface TriePathCacheManipulator extends Pinenut {
    void insert ( GUID guid, String path );

    void insertLongPath( GUID guid, String path, String longPath );

    void remove ( GUID guid );

    String getPath ( GUID guid );

    GUID getNode ( String path );

    GUID queryGUIDByPath( String path );
}
