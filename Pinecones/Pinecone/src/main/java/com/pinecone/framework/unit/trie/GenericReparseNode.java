package com.pinecone.framework.unit.trie;

import com.pinecone.framework.system.prototype.Pinenut;

public class GenericReparseNode<V> extends ArchTrieNode<V > implements ReparseNode<V > {
    protected TrieNode              target  ;
    protected String                path    ;

    public <K extends String > GenericReparseNode( TrieNode<V> parent, String path, AbstractTrieMap<K, V> trieMap ) {
        super( parent, trieMap );
        TrieNode node = this.getTrieMap().queryNode(path);
//        if ( node != null && node.isEnd ) {
//            this.target = node;
//        }
//        else {
//            throw new RuntimeException("Target node does not exist or is not a leaf node.");
//        }
        this.target = node;
        this.path = path;
    }


    public TrieNode getTarget() {
        return this.target;
    }

    public void setTarget( TrieNode target ) {
        this.target = target;
    }

    public String getPath() {
        return path;
    }

    public void setPath( String path ) {
        this.path = path;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
