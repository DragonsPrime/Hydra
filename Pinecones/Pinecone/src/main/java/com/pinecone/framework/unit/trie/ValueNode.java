package com.pinecone.framework.unit.trie;

public interface ValueNode<V > extends TrieNode<V > {
    V getValue();

    void setValue( V value );

    @Override
    default ValueNode<V > evinceValue() {
        return this;
    }
}
