package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.entity.ElementNode;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FileSystemAttributes extends Pinenut ,Map<String, String >{
    GUID getGuid();

    void setGuid( GUID guid );

    String getAttribute( String key );

    void setAttribute( String key, String value );

    Map<String, String > getAttributes();

    void setAttributes( Map<String, String > attributes );

    ElementNode parentElement();

    @Override
    default boolean isEmpty() {
        return this.getAttributes().isEmpty();
    }

    @Override
    default int size() {
        return this.getAttributes().size();
    }

    @Override
    default boolean containsKey( Object key ) {
        return this.getAttributes().containsKey( key );
    }


    @Override
    default boolean containsValue( Object value ) {
        return this.getAttributes().containsValue(value);
    }

    @Override
    default String get( Object key ) {
        return this.getAttributes().get(key);
    }

    @Override
    default Set<String > keySet() {
        return this.getAttributes().keySet();
    }

    @Override
    default Collection<String > values() {
        return this.getAttributes().values();
    }

    @Override
    default Set<Map.Entry<String, String >> entrySet() {
        return this.getAttributes().entrySet();
    }


    String insert( String key, String value ) ;

    String update( String key, String value ) ;
}
