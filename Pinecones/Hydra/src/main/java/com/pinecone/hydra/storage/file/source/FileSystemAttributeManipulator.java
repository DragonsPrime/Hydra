package com.pinecone.hydra.storage.file.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.FileSystemAttributes;
import com.pinecone.hydra.storage.file.entity.ElementNode;

import java.util.List;
import java.util.Map;

public interface FileSystemAttributeManipulator extends Pinenut {
    void insertAttribute(GUID guid, String key, String value );

    List<Map<String, Object >> getAttributesByGuid(GUID guid );

    void updateAttribute( GUID guid, String key, String value );

    void remove( GUID guid );

    FileSystemAttributes getAttributes(GUID guid, ElementNode element );

    default void insert( FileSystemAttributes attributes) {
        for ( Map.Entry<String, String> entry : attributes.getAttributes().entrySet() ) {
            this.insertAttribute( attributes.getGuid(), entry.getKey(), entry.getValue() );
        }
    }

    default void update( FileSystemAttributes attributes) {
        for ( Map.Entry<String, String> entry : attributes.getAttributes().entrySet() ) {
            this.updateAttribute( attributes.getGuid(), entry.getKey(), entry.getValue() );
        }
    }


    boolean containsKey ( GUID guid, String key );

    void clearAttributes( GUID guid );

    void removeAttributeWithValue( GUID guid, String key, String value );

    void removeAttribute( GUID guid, String key );
}
