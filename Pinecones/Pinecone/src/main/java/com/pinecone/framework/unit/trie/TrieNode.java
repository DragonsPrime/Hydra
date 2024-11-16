package com.pinecone.framework.unit.trie;

import com.pinecone.framework.system.prototype.Pinenut;

public interface TrieNode<V > extends Pinenut {
    boolean isLeaf();

    TrieNode parent();

    TrieMap<String, V> getTrieMap();

    default DirectoryNode<V > evinceDirectory() {
        return null;
    }

    default ValueNode<V > evinceValue() {
        return null;
    }

    default GenericReparseNode evinceReparse() {
        return null;
    }
}
