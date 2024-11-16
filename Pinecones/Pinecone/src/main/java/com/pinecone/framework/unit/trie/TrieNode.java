package com.pinecone.framework.unit.trie;

import com.pinecone.framework.system.prototype.Pinenut;

public interface TrieNode<V > extends Pinenut {
    boolean isLeaf();

    TrieNode<V > parent();

    TrieMap<String, V> getTrieMap();

    String getNodeName();

    String getFullName();

    String getNamespace();

    default DirectoryNode<V > evinceDirectory() {
        return null;
    }

    default ValueNode<V > evinceValue() {
        return null;
    }

    default ReparseNode<V > evinceReparse() {
        return null;
    }

    String getTypeName();
}
