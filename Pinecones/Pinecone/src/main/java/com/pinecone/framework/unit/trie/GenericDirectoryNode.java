package com.pinecone.framework.unit.trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;

public class GenericDirectoryNode<V > extends ArchTrieNode<V > implements DirectoryNode<V > {
    protected Map<String, TrieNode<V > > mChildren;

    /**
     * Root constructor.
     */
    public <K extends String > GenericDirectoryNode( Map<String, TrieNode<V > > children, TrieMap<K, V > map ) {
        this( null, children, null, map );
    }

    public <K extends String > GenericDirectoryNode( String szKey, Map<String, TrieNode<V > > children, TrieNode<V> parent, TrieMap<K, V > map ) {
        super( szKey, parent, map );
        this.mChildren = children;
    }

    @Override
    public int childrenLeafSize() {
        int leafCount = 0;

        for ( TrieNode<V> child : this.mChildren.values() ) {
            DirectoryNode<V> directoryNode = child.evinceDirectory();

            if ( directoryNode != null ) {
                leafCount += directoryNode.childrenLeafSize();
            }
            else {
                if ( child.isLeaf() ) {
                    ++leafCount;
                }
            }
        }

        return leafCount;
    }

    @Override
    public Map<String, TrieNode<V > > children() {
        return this.mChildren;
    }

    @Override
    public Map<String, TrieNode<V > > segmentMap() {
        return this.mChildren;
    }

    @Override
    public TrieNode<V > get( String szSegName ) {
        return this.mChildren.get( szSegName );
    }

    @Override
    public void put( String szSegName, TrieNode<V > node ) {
        this.mChildren.put( szSegName, node );
    }

    @Override
    public void putIfAbsent( String szSegName, TrieNode<V > node ) {
        this.mChildren.putIfAbsent( szSegName, node );
    }

    protected void notifyMapChildrenEliminated( int nFatalities ) {
        //( (UniTrieMaptron) this.mTrieMap ).notifyChildrenEliminated( nFatalities );
    }

    @Override
    public TrieNode<V > remove( String szSegName ) {
//        int nFatalities = 1;
//        DirectoryNode<V > childDir = this.getDirectory( szSegName );
//        if( childDir != null ) {
//            nFatalities = childDir.childrenLeafSize();
//        }
//        TrieNode<V > legacy = this.mChildren.remove( szSegName );
//        this.notifyMapChildrenEliminated( nFatalities ); // Cascading leafs.
//        return legacy;

        return this.mChildren.remove( szSegName );
    }

    @Override
    public boolean isEmpty() {
        return this.mChildren.isEmpty();
    }

    @Override
    public int size() {
        return this.mChildren.size();
    }

    @Override
    public void purge() {
        //int nFatalities = this.childrenLeafSize();
        this.mChildren.clear();
        //this.notifyMapChildrenEliminated( nFatalities );
    }

    @Override
    public Set<Map.Entry<String, TrieNode<V >> > entrySet() {
        return this.mChildren.entrySet();
    }

    @Override
    public List<ValueNode<V>> listValueNodes() {
        List<ValueNode<V>>             list = new ArrayList<>();
        Collection<TrieNode<V > > trieNodes = this.values();
        for( TrieNode<V > node : trieNodes ) {
            ValueNode<V> vn = node.evinceValue();
            if( vn != null ) {
                list.add( vn );
            }
        }
        return list;
    }

    @Override
    public List<V> listValues() {
        List<V>                        list = new ArrayList<>();
        Collection<TrieNode<V > > trieNodes = this.values();
        for( TrieNode<V > node : trieNodes ) {
            ValueNode<V> vn = node.evinceValue();
            if( vn != null ) {
                list.add( vn.getValue() );
            }
        }
        return list;
    }

    @Override
    public List<DirectoryNode<V >> listDirectories() {
        List<DirectoryNode<V >>            list = new ArrayList<>();
        Collection<TrieNode<V > >     trieNodes = this.values();
        for( TrieNode<V > node : trieNodes ) {
            DirectoryNode<V> dir = node.evinceDirectory();
            if( dir != null ) {
                list.add( dir );
            }
        }
        return list;
    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "FullName"      , this.getFullName()                ),
                new KeyValue<>( "Type"          , ReparseNode.class.getSimpleName() ),
                new KeyValue<>( "ChildrenSize"  , this.size()                       )
        } );
    }
}
