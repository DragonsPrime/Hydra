package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FileSystemAttributes;
import com.pinecone.hydra.storage.file.source.FileSystemAttributeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
@IbatisDataAccessObject
public interface FileSystemAttributeMapper extends FileSystemAttributeManipulator {
    @Insert( "INSERT INTO `hydra_registry_node_attributes` (`guid`, `key`, `value`) VALUES (#{guid}, #{key}, #{value})" )
    void insertAttribute(GUID guid, String key, String value );

    List<Map<String, Object >> getAttributesByGuid(GUID guid );

    void updateAttribute( GUID guid, String key, String value );
    void remove( GUID guid );

    default FileSystemAttributes getAttributes(GUID guid, ElementNode element ){
        return null;
    };

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
