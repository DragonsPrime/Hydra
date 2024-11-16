package com.pinecone.framework.unit.trie;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSON;

/**
 *  Pinecone Ursus For Java UniTrieMaptron
 *  SharedList Author: Ken, DragonKing
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Thanks for Ken`s contribution.
 *  **********************************************************
 */
public class UniTrieMaptron<K extends String, V > extends AbstractTrieMap<K, V > implements TrieMap<K, V >, Cloneable {
    protected transient DirectoryNode<V >                           mRoot;
    protected final transient Supplier<Map<String, TrieNode<V > > > mMapSupplier;
    protected transient int                                         mnSize;
    protected transient TrieSegmentor                               mSegmentor;

    protected transient Set<Entry<K, V> >                           mEntrySet;
    protected transient Set<K>                                      mKeySet;
    protected transient Collection<V>                               mValues;

    @SuppressWarnings( "unchecked" )
    public UniTrieMaptron( Supplier mapSupplier, TrieSegmentor segmentor ) {
        if ( mapSupplier == null ) {
            throw new IllegalArgumentException( "Map supplier cannot be null." );
        }
        this.mMapSupplier  = mapSupplier;
        this.mRoot         = new GenericDirectoryNode( this.mMapSupplier.get(), this );
        this.mnSize         = 0;
        this.mSegmentor    = segmentor;
    }

    public UniTrieMaptron( Supplier mapSupplier ) {
        this( mapSupplier, TrieSegmentor.DefaultSegmentor );
    }

    public UniTrieMaptron( TrieSegmentor segmentor ) {
        this( TreeMap::new, segmentor );
    }

    public UniTrieMaptron() {
        this( (Supplier) TreeMap::new );
    }




    @SuppressWarnings( "unchecked" )
    protected V convertValue( Object value ) {
        return ( V ) value;
    }

    protected String getStringKey( Object key ) {
        if ( key instanceof String ) {
            return  (String) key;
        }

        return key.toString();
    }

    @Override
    public boolean hasOwnProperty( Object elm ) {
        return this.containsKey( elm );
    }

    @Override
    public V put( K key, V value ) {
        return this.putEntity( key, value );
    }

    public V makeSymbolic ( K key, K target ) {
        ReparseNode<V> p = new GenericReparseNode<>( null, null, target,this );
        return this.putEntity( key, p );
    }

    public V putEntity( K key, Object value ) {
        if ( key == null ) {
            throw new IllegalArgumentException( "Key cannot be null." );
        }
        String[] segments         = this.mSegmentor.segments( key );
        TrieNode<V> node          = this.mRoot;
        DirectoryNode<V> dir      = this.mRoot;
        TrieNode<V> parent        = this.mRoot;

        String szLeafKey          = null;
        for ( int i = 0; i < segments.length; ++i ) {
            String segment = segments[ i ];

            if ( i < segments.length - 1 ) {
                node = dir.get( segment );
                if( node == null ) {
                    DirectoryNode<V> neo = new GenericDirectoryNode<>( segment, this.mMapSupplier.get() ,parent, this );
                    dir.put( segment, neo );
                    node = neo;
                    dir  = neo;
                }
                else {
                    dir = node.evinceDirectory();
                    if( dir == null ) {
                        throw new IllegalArgumentException( "Path given is not a full-directory insertion path." );
                    }
                }
            }
            else { // Leaf Node
                szLeafKey = segment;
                node = dir.get( segment );
                if( node == null ) {
                    TrieNode<V> neo;
                    if ( value instanceof ReparseNode ) {
                        ReparseNode dummy = (ReparseNode) value;
                        neo = new GenericReparseNode<>( segment, dir, dummy.getReparsePointer(),this );
                    }
                    else {
                        neo = new GenericValueNode<>( segment, this.convertValue( value ), parent, this );
                    }
                    dir.put( segment, neo );
                    ++this.mnSize;
                    return null; // Insertion
                }
            }
            parent = node;
        }

        // Modification
        ValueNode<V > vn = node.evinceValue();
        if( vn != null ) {
            V legacyValue = vn.getValue();
            vn.setValue( this.convertValue( value ) );

            return legacyValue;
        }

        ReparseNode<V > rn = node.evinceReparse();
        if( rn != null ) {
            TrieNode<V > revealed = rn.reparse();
            if( revealed != null ) {
                vn = revealed.evinceValue();
                if( vn != null ) {
                    V legacyValue = vn.getValue();
                    vn.setValue( this.convertValue( value ) );

                    return legacyValue;
                }
            }
        }

        DirectoryNode<V > dn = node.evinceDirectory();
        if( dn != null ) {
            TrieNode<V>       pp = dn.parent();
            if( pp == null ) {
                pp = this.mRoot;
            }
            DirectoryNode<V > pd = pp.evinceDirectory();
            pd.remove( szLeafKey );
            pd.put( szLeafKey, new GenericValueNode<>( dn.getNodeName(), this.convertValue( value ), pp, this ) );
        }

        return null;
    }

    @Override
    public V get( Object key ) {
        String szKey = this.getStringKey( key );

        TrieNode<V> node = this.queryNode( szKey );
        if ( node == null ) {
            return null;
        }

        ValueNode<V > vn = node.evinceValue();
        if ( vn != null ){
            return vn.getValue();
        }

        ReparseNode<V > rp = node.evinceReparse();
        if ( rp != null ){
            TrieNode<V > revealed = rp.reparse();
            if( revealed != null ) {
                vn = revealed.evinceValue();
                if( vn != null ) {
                    return vn.getValue();
                }
            }
        }

        return null;
    }

    @Override
    public boolean containsKey( Object key ) {
        String szKey = this.getStringKey( key );

        return this.queryNode( szKey ) != null;
    }

    @Override
    public boolean containsValue( Object value ) {
        return this.dfsContainsValue( this.mRoot, value );
    }

    private boolean dfsContainsValue( TrieNode<V > node, Object value ) {
        if ( node == null ) {
            return false;
        }

        DirectoryNode<V > directory = node.evinceDirectory();
        if ( directory != null ) {
            for ( TrieNode<V > childNode : directory.children().values() ) {
                if ( this.dfsContainsValue( childNode, value ) ) {
                    return true;
                }
            }
        }
        else {
            ValueNode<V > vn = node.evinceValue();
            if( vn != null ) {
                return vn.getValue().equals( value );
            }

            ReparseNode<V > rp = node.evinceReparse();
            if ( rp != null ){
                TrieNode<V > revealed = rp.reparse();
                if( revealed != null ) {
                    vn = revealed.evinceValue();
                    if( vn != null ) {
                        return vn.getValue().equals( value );
                    }
                }
            }
        }

        return false;
    }

    @Override
    public V remove( Object key ) {
        String szKey = this.getStringKey( key );

        return this.remove( this.mRoot, this.mSegmentor.segments( szKey ), 0 );
    }

    protected V remove( TrieNode<V > node, String[] segments, int depth ) {
        if ( node == null || depth >= segments.length ) {
            return null;
        }

        String segment = segments[ depth ];
        DirectoryNode<V > directory = node.evinceDirectory();
        if ( directory == null ) {
            return null;
        }

        TrieNode<V > childNode = directory.get(segment);

        if ( depth == segments.length - 1 ) {
            if ( childNode == null ) {
                return null; // Illegal path.
            }

            directory.remove( segment ); // <= Fatalities statistics therein.

            ValueNode<V> valueNode = childNode.evinceValue();
            if ( valueNode != null ) {
                return valueNode.getValue();
            }
            return null;
        }


        return this.remove( childNode, segments, depth + 1 );


//        if ( node == null ) {
//            return null;
//        }
//
//        if ( depth == segments.length ) {
//            if ( !node.isEnd ) {
//                return null;
//            }
//            node.isEnd = false;
//            V oldValue = this.convertValue( node.value );
//            node.value = null;
//            --this.mnSize;
//            return oldValue;
//        }
//
//        String segment = segments[depth];
//        TrieNode nextNode = node.children.get( segment );
//        V result = this.remove( nextNode, segments, depth + 1 );
//
//        if ( nextNode != null && nextNode.children.isEmpty() && !nextNode.isEnd ) {
//            node.children.remove( segment );
//        }
//
//        return result;
    }

    @Override
    public void putAll( Map<? extends K, ? extends V> m ) {
        for ( Entry<? extends K, ? extends V> entry : m.entrySet() ) {
            this.put( entry.getKey(), entry.getValue() );
        }
    }

    @Override
    public void clear() {
        this.mRoot.purge();
        this.mnSize = 0;
    }

    protected void notifyChildrenEliminated( int nFatalities ) {
        this.mnSize -= nFatalities;
    }

    @Override
    public int size() {
        return this.mnSize;
    }

    @Override
    public boolean isEmpty() {
        return this.mnSize == 0;
    }

    @Override
    public TrieNode<V> queryNode( String path ) {
        String[] segments    = this.mSegmentor.segments( path );
        DirectoryNode<V> dir = this.mRoot;
        TrieNode<V>     node = this.mRoot;

        for ( int i = 0; i < segments.length; ++i ) {
            String segment = segments[ i ];

            if ( i < segments.length - 1 ) {
                node = dir.get( segment );
                dir  = node.evinceDirectory();
                if( dir == null ) {
                    return null; // Illegal path.
                }
            }
            else {
                return dir.get( segment );
            }
        }
        return null;
    }

    @Override
    public Set<K> keySet() {
        Set<K >  es = this.mKeySet;
        return (es != null) ? es : (this.mKeySet = new KeySet());
    }

    @Override
    public Collection<V> values() {
        Collection<V> vs = this.mValues;
        if (vs == null) {
            vs = new Values();
            this.mValues = vs;
        }
        return vs;
    }

    @Override
    public Set<Entry<K,V > > entrySet() {
        Set<Entry<K,V > >  es = this.mEntrySet;
        return (es != null) ? es : (this.mEntrySet = new EntrySet());
    }




    class EntrySet extends AbstractSet<Entry<K, V>> {

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return UniTrieMaptron.this.size();
        }

        @Override
        public boolean contains( Object o ) {
            if ( !( o instanceof Map.Entry ) ) {
                return false;
            }
            Entry<?, ?> entry = ( Entry<?, ?> ) o;
            Object value = UniTrieMaptron.this.get( entry.getKey() );
            return Objects.equals( value, entry.getValue() );
        }

        @Override
        public boolean remove( Object o ) {
            if ( !( o instanceof Map.Entry ) ) {
                return false;
            }
            Entry<?, ?> entry = ( Entry<?, ?> ) o;
            K key = ( K ) entry.getKey();
            V currentValue = UniTrieMaptron.this.get( key );
            if ( Objects.equals( currentValue, entry.getValue() ) ) {
                UniTrieMaptron.this.remove( key );
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            UniTrieMaptron.this.clear();
        }
    }

    class EntryIterator implements Iterator<Entry<K, V>> {
        private final Map<String, TrieNode<V > > dummyTerminationMap = Map.of();

        private final Deque<Iterator<Entry<String, TrieNode<V >>>> stack;
        private final Deque<StringBuilder> pathStack;
        private Entry<K, V> nextEntry;
        private StringBuilder currentPath;

        public EntryIterator() {
            this.stack = new ArrayDeque<>();
            this.pathStack = new ArrayDeque<>();
            this.stack.push( UniTrieMaptron.this.mRoot.children().entrySet().iterator() );
            this.currentPath = new StringBuilder();

            this.advance();
        }

        @SuppressWarnings( "unchecked" )
        private void advance() {
            this.nextEntry = null;

            while ( !this.stack.isEmpty() ) {
                Iterator<Entry<String, TrieNode<V >>> iterator = this.stack.peek();
                if ( !iterator.hasNext() ) {
                    this.stack.pop();
                    if ( !this.pathStack.isEmpty() ) {
                        this.currentPath = this.pathStack.pop();
                    }
                    continue;
                }

                Entry<String, TrieNode<V >> entry = iterator.next();
                TrieNode<V > node = entry.getValue();
                String segment = entry.getKey();

                this.pathStack.push( new StringBuilder( this.currentPath ) );
                if ( this.currentPath.length() > 0 ) {
                    this.currentPath.append( UniTrieMaptron.this.mSegmentor.getSeparator() );
                }
                this.currentPath.append( segment );

//                while ( node.value instanceof TrieReparseNode ) {
//                    TrieReparseNode<K, V> reparseNode = ( TrieReparseNode<K, V> ) node.value;
//                    node = UniTrieMaptron.this.getNode( reparseNode.getPath() );
//                    if ( node == null ) {
//                        break;
//                    }
//                }

                if ( node == null ) {
                    continue;
                }

                DirectoryNode<V > dir = node.evinceDirectory();
                if( dir != null ) {
                    this.stack.push( dir.children().entrySet().iterator() );
                }
                else {
                    ValueNode<V> vn = node.evinceValue();
                    if( vn != null ) {
                        this.nextEntry = new AbstractMap.SimpleEntry<>( ( K ) this.currentPath.toString(), vn.getValue() );
                        this.stack.push( this.dummyTerminationMap.entrySet().iterator() );
                    }

                    ReparseNode<V> rn = node.evinceReparse();
                    if( rn != null ) {
                        this.nextEntry = new AbstractMap.SimpleEntry( this.currentPath.toString(), rn );
                        this.stack.push( this.dummyTerminationMap.entrySet().iterator() );
                    }

                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.nextEntry != null;
        }

        @Override
        public Entry<K, V> next() {
            if ( !this.hasNext() ) {
                throw new NoSuchElementException();
            }

            Entry<K, V> entry = this.nextEntry;
            this.advance();
            return entry;
        }
    }

    class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return UniTrieMaptron.this.size();
        }

        @Override
        public boolean contains( Object o ) {
            return UniTrieMaptron.this.containsKey( o );
        }

        @Override
        public boolean remove( Object o ) {
            return UniTrieMaptron.this.remove( o ) != null;
        }

        @Override
        public void clear() {
            UniTrieMaptron.this.clear();
        }
    }

    class KeyIterator implements Iterator<K> {
        private final Iterator<Entry<K, V>> entryIterator;

        public KeyIterator() {
            this.entryIterator = UniTrieMaptron.this.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.entryIterator.hasNext();
        }

        @Override
        public K next() {
            return this.entryIterator.next().getKey();
        }
    }

    class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                private final Iterator<Entry<K, V>> entryIterator = UniTrieMaptron.this.entrySet().iterator();

                @Override
                public boolean hasNext() {
                    return this.entryIterator.hasNext();
                }

                @Override
                public V next() {
                    return this.entryIterator.next().getValue();
                }
            };
        }

        @Override
        public int size() {
            return UniTrieMaptron.this.size();
        }

        @Override
        public boolean contains( Object o ) {
            return UniTrieMaptron.this.containsValue(o);
        }
    }

    @Override
    public TrieMap<K, V> clone() {
        try {
            @SuppressWarnings("unchecked")
            UniTrieMaptron<K, V> clonedMap = (UniTrieMaptron<K, V>) super.clone();

            clonedMap.mRoot     = this.cloneDirectoryNode( this.mRoot, clonedMap, null );

            clonedMap.mEntrySet = null;
            clonedMap.mKeySet   = null;
            clonedMap.mValues   = null;
            clonedMap.mnSize    = this.mnSize;

            return clonedMap;
        }
        catch ( CloneNotSupportedException e ) {
            throw new AssertionError( "Clone not supported", e );
        }
    }

    protected DirectoryNode<V> cloneDirectoryNode( DirectoryNode<V> original, TrieMap<K, V> pm, TrieNode<V > parent ) {
        if ( original == null ) {
            return null;
        }

        Map<String, TrieNode<V>> clonedChildren = this.mMapSupplier.get();
        DirectoryNode<V > neo = new GenericDirectoryNode<>( original.getNodeName(), clonedChildren, parent, pm );
        for ( Map.Entry<String, TrieNode<V>> entry : original.children().entrySet() ) {
            TrieNode<V> clonedChild = this.cloneTrieNode( entry.getValue(), pm, neo );
            clonedChildren.put( entry.getKey(), clonedChild );
        }

        return neo;
    }

    protected TrieNode<V> cloneTrieNode( TrieNode<V> original, TrieMap<K, V> pm, TrieNode<V > parent ) {
        if ( original == null ) {
            return null;
        }

        DirectoryNode<V> directoryNode = original.evinceDirectory();
        if ( directoryNode != null ) {
            return this.cloneDirectoryNode( directoryNode, pm, parent );
        }

        ValueNode<V> valueNode = original.evinceValue();
        if ( valueNode != null ) {
            return new GenericValueNode<>( original.getNodeName(), valueNode.getValue(), parent, pm );
        }

        ReparseNode<V > rp = original.evinceReparse();
        if ( rp != null ){
            return new GenericReparseNode<>( original.getNodeName(), parent, rp.getReparsePointer(), pm );
        }

        return null;
    }

    @Override
    public String toJSONString() {
        return JSON.stringify( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String getSeparator(){
        return this.mSegmentor.getSeparator();
    }
}
