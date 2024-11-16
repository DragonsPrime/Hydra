package com.pinecone.framework.unit.trie;

public interface ReparseNode<V > extends TrieNode<V > {
    String getReparsePointer();

    void setReparsePointer( String path );

    TrieNode<V > reparse();

    @Override
    default ReparseNode<V > evinceReparse() {
        return this;
    }

    @Override
    default String getTypeName() {
        return ReparseNode.class.getSimpleName();
    }
}
