package com.pinecone.framework.unit.trie;

import com.pinecone.framework.util.json.JSON;

public class GenericValueNode<V > extends ArchTrieNode<V > implements ValueNode<V > {
    protected V value;

    public <K extends String > GenericValueNode( String szKey, V value, TrieNode<V> parent, TrieMap<K, V > map ) {
        super( szKey, parent, map );
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

    @Override
    public String toJSONString() {
        return JSON.stringify( this.value );
    }
}
