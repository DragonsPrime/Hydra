package com.pinecone.framework.unit.trie;

public abstract class ArchTrieNode<V > implements TrieNode<V > {
    protected String                mszKey;
    protected TrieNode<V>           mParent;
    protected TrieMap<String, V >   mTrieMap;

    @SuppressWarnings( "unchecked" )
    public <K extends String > ArchTrieNode( String szKey, TrieNode<V> parent, TrieMap<K, V > map ) {
        this.mParent  = parent;
        this.mTrieMap = (TrieMap<String, V>) map;
        this.mszKey   = szKey;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String getNodeName() {
        return this.mszKey;
    }

    @Override
    public String getNamespace() {
        TrieNode<V> p = this.mParent;
        StringBuilder sb = new StringBuilder();
        String separator = this.getTrieMap().getSeparator();

        if( p == null || p.parent() == null ) {
            return null;
        }

        while ( true ) {
            if ( sb.length() > 0 ) {
                sb.insert( 0, separator );
            }
            sb.insert(0, p.getNodeName());
            p = p.parent();
            if( p == null || p.parent() == null ) {
                break;
            }
        }

        return sb.toString();
    }

    @Override
    public String getFullName() {
        String ns = this.getNamespace();
        if( ns != null ) {
            return ns + this.getTrieMap().getSeparator() + this.getNodeName();
        }
        return this.getNodeName();
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
