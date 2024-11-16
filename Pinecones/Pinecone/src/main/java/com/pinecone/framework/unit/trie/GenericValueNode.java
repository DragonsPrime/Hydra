package com.pinecone.framework.unit.trie;

public class GenericValueNode<V > extends ArchTrieNode<V > implements ValueNode<V > {
    protected V value;

    public <K extends String > GenericValueNode( V value, TrieNode<V> parent, TrieMap<K, V > map ) {
        super( parent, map );
        this.value = value;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public void setValue( V value ) {
        this.value = value;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
