package com.pinecone.framework.unit.trie;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectoryNode<V > extends TrieNode<V > {
    Map<String, TrieNode<V > > children();

    default DirectoryNode<V > getDirectory( String szSegName ) {
        TrieNode<V > n = this.get( szSegName );
        if( n != null ) {
            return n.evinceDirectory();
        }
        return null;
    }

    default ValueNode<V > getValue( String szSegName ) {
        TrieNode<V > n = this.get( szSegName );
        if( n != null ) {
            return n.evinceValue();
        }
        return null;
    }

    default ReparseNode getReparse( String szSegName ) {
        TrieNode n = this.get( szSegName );
        if( n != null ) {
            return n.evinceReparse();
        }
        return null;
    }

    TrieNode<V > get( String szSegName );

    void put( String szSegName, TrieNode<V > node );

    void putIfAbsent( String szSegName, TrieNode<V > node );

    TrieNode<V > remove( String szSegName );

    boolean isEmpty();

    int size();

    int childrenLeafSize();

    void purge();

    Set<Map.Entry<String, TrieNode<V >> > entrySet();

    default Collection<TrieNode<V > > values() {
        return this.children().values();
    }

    default Set<String > keySet(){
        return this.children().keySet();
    }

    default Collection<TrieNode<V > > listItems() {
        return this.values();
    }

    List<ValueNode<V > > listValueNodes();

    List<V > listValues();

    List<DirectoryNode<V > > listDirectories();

    @Override
    default DirectoryNode<V > evinceDirectory() {
        return this;
    }

    @Override
    default String getTypeName() {
        return DirectoryNode.class.getSimpleName();
    }
}
