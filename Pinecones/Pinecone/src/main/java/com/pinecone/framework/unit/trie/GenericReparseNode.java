package com.pinecone.framework.unit.trie;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;

public class GenericReparseNode<V> extends ArchTrieNode<V > implements ReparseNode<V > {
    protected String                mszReparsePointer    ;

    public <K extends String > GenericReparseNode( String szKey, TrieNode<V> parent, String szReparsePointer, TrieMap<K, V> trieMap ) {
        super( szKey, parent, trieMap );
        this.mszReparsePointer = szReparsePointer;
    }

    @Override
    public String getReparsePointer() {
        return mszReparsePointer;
    }

    @Override
    public void setReparsePointer( String path ) {
        this.mszReparsePointer = path;
    }

    @Override
    public TrieNode<V > reparse() {
        String szReparsePointer = this.mszReparsePointer;
        while ( true ) {
            TrieNode<V >    revealed = this.getTrieMap().queryNode( szReparsePointer );
            if( revealed != null ) {
                ReparseNode<V > reparsed = revealed.evinceReparse();
                if( reparsed != null ) {
                    szReparsePointer = reparsed.getReparsePointer();
                    continue;
                }

                return revealed;
            }
            else {
                return null;
            }
        }
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String toString() {
        TrieNode<V > revealed = this.reparse();
        if( revealed != null ) {
            return revealed.toString();
        }
        return null;
    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "FullName"      , this.getFullName()                ),
                new KeyValue<>( "Type"          , ReparseNode.class.getSimpleName() ),
                new KeyValue<>( "ReparsePoint"  , this.getReparsePointer()          )
        } );
    }
}
