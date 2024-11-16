package com.pinecone.framework.unit.trie;

import com.pinecone.framework.system.prototype.Pinenut;

public interface TrieSegmentor extends Pinenut {
    TrieSegmentor PathSegmentor    = new SeparatedSegmentor();

    TrieSegmentor ObjectSegmentor  = new SeparatedSegmentor( "." );

    TrieSegmentor DefaultSegmentor = TrieSegmentor.PathSegmentor;


    String[] segments( String szPathKey );

    String getSeparator();
}
