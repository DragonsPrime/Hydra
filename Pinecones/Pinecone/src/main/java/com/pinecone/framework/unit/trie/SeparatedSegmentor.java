package com.pinecone.framework.unit.trie;

public class SeparatedSegmentor implements TrieSegmentor {
    protected String separator;

    public SeparatedSegmentor( String szSeparator ) {
        this.separator = szSeparator;
    }

    public SeparatedSegmentor() {
        this( "/" );
    }

    @Override
    public String[] segments( String szPathKey ) {
        return szPathKey.split( this.separator );
    }

    @Override
    public String getSeparator() {
        return this.separator;
    }
}
