package com.pinecone.framework.unit.trie;

public abstract class ArchTrieNode<V > implements TrieNode<V > {
    protected TrieNode<V>           mParent;
    protected TrieMap<String, V >   mTrieMap;

    @SuppressWarnings( "unchecked" )
    public <K extends String > ArchTrieNode( TrieNode<V> parent, TrieMap<K, V > map ) {
        this.mParent  = parent;
        this.mTrieMap = (TrieMap<String, V>) map;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public TrieNode<V> parent() {
        return this.mParent;
    }

    @Override
    public TrieMap<String, V> getTrieMap() {
        return this.mTrieMap;
    }
}
