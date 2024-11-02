package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.system.prototype.Prototype;
import com.pinecone.framework.system.prototype.TypeIndex;
import com.pinecone.framework.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.AbstractSet;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.NoSuchElementException;


public class JSONArraytron extends ArchJSONArray implements JSONArray {
    private List<Object > mList;
    protected transient Set<Map.Entry<Integer, Object > > entrySet ;

    public JSONArraytron() {
        this.mList = new ArrayList<>();
    }

    public JSONArraytron( ArchCursorParser x ) throws JSONException {
        this();
        this.jsonDecode0( x );
    }

    public JSONArraytron( String source ) throws JSONException {
        this(new JSONCursorParser(source));
    }

    public JSONArraytron( Collection collection ) {
        this.mList = new ArrayList<>();
        if ( collection != null ) {
            Iterator iter = collection.iterator();

            while( iter.hasNext() ) {
                this.mList.add(JSONUtils.wrapValue( iter.next()) );
            }
        }

    }

    public JSONArraytron( Object array ) throws JSONException {
        this();
        if (!array.getClass().isArray()) {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        } else {
            int length = Array.getLength(array);

            for(int i = 0; i < length; ++i) {
                this.put(JSONUtils.wrapValue(Array.get(array, i)));
            }

        }
    }

    public JSONArraytron( List<Object > array, boolean bAssimilateMode ) throws JSONException {
        if( bAssimilateMode ){
            this.mList = array;
        }
        else {
            this.mList = new ArrayList<>();
            if ( array != null ) {
                for ( Object o : array ) {
                    this.put( JSONUtils.wrapValue(o) );
                }
            }
        }
    }

    public JSONArraytron( List<Object > array ) throws JSONException {
        this( array, false );
    }


    @Override
    protected void jsonDecode0( ArchCursorParser x ) throws JSONException {
        JSONArrayDecoder.INNER_JSON_ARRAY_DECODER.decode( this, null, null, x );
    }

    @Override
    public JSONArraytron jsonDecode( ArchCursorParser x ) throws JSONException {
        this.clear();
        this.jsonDecode0( x );
        return this;
    }

    @Override
    public JSONArraytron jsonDecode( String source ) throws JSONException {
        return this.jsonDecode( new JSONCursorParser(source) );
    }

    @Override
    public void assimilate( List<Object > that ){
        this.mList = that;
    }

    @Override
    public List<Object > getArray(){
        return this.mList;
    }



    /** Basic List<Object> **/
    @Override
    public int size() {
        return this.mList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.mList.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return this.mList.contains(o);
    }

    @Override
    public Iterator<Object > iterator() {
        return this.mList.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.mList.toArray();
    }

    @Override
    public <T> T[] toArray( T[] a ) {
        return (T[])this.mList.toArray(a);
    }


    @Override
    protected boolean innerListAdd( Object e) {
        return this.mList.add( e );
    }

    @Override
    public void clear() {
        this.mList.clear();
    }

    @Override
    protected boolean innerListRemove( Object index ) {
        return this.mList.remove( index );
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        return this.mList.containsAll(c);
    }

    @Override
    public boolean addAll( Collection<?> c ) {
        return this.mList.addAll(c);
    }

    @Override
    public boolean addAll( int index, Collection<?> c ) {
        return this.mList.addAll(index, c);
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        return this.mList.removeAll(c);
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        return this.mList.retainAll(c);
    }





    @Override
    public void add( int index, Object element ) {
        this.mList.add( index, element );
    }

    @Override
    public int indexOf( Object o ) {
        return this.mList.indexOf(o);
    }

    @Override
    public int lastIndexOf( Object o ) {
        return this.mList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return this.mList.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return this.mList.listIterator(index);
    }

    @Override
    public List<Object> subList( int fromIndex, int toIndex ) {
        return this.mList.subList(fromIndex, toIndex);
    }


    @Override
    protected Object innerListGet( int key ) {
        return this.mList.get( key );
    }

    @Override
    protected Object innerListSet( int index, Object element ) {
        return this.mList.set( index, element );
    }

    @Override
    public JSONArraytron put( Collection value ) {
        this.put((Object)(new JSONArraytron(value)));
        return this;
    }

    @Override
    public JSONArraytron put( int index, Collection value ) throws JSONException {
        this.put(index, (Object)( new JSONArraytron(value)) );
        return this;
    }

    @Override
    public JSONArraytron put( int index, Map value ) throws JSONException {
        this.put(index, (Object)(new JSONMaptron(value)));
        return this;
    }

    @Override
    public Set<?> entrySet() {
        Set<Map.Entry<Integer,Object > > es;
        return (es = this.entrySet) == null ? (this.entrySet = new ListEntrySet()) : es;
    }



    @Override
    public JSONArraytron clone() {
        JSONArraytron that = (JSONArraytron) super.clone();
        that.mList = new ArrayList<>();
        for ( Object row : this.mList ) {
            that.put( JSONUtils.cloneElement( row ) );
        }
        return that;
    }

    @Override
    public Writer write( Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        return JSONEncoder.BASIC_JSON_ENCODER.write( this.mList, writer, nIndentFactor, nIndentBlankNum );
    }


    protected static boolean valEquals( Object o1, Object o2 ) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    protected static class JSONArrayEntry implements Map.Entry<Integer, Object >, Pinenut {
        Integer key;
        Object  value;

        JSONArrayEntry( Integer key, Object value ) {
            this.key   = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return this.key;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public Object setValue( Object value ) {
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public void setKey( Integer key ) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;

            return valEquals(key,e.getKey()) && valEquals(value,e.getValue());
        }

        @Override
        public int hashCode() {
            int keyHash = (key==null ? 0 : key.hashCode());
            int valueHash = (value==null ? 0 : value.hashCode());
            return keyHash ^ valueHash;
        }

        @Override
        public String toString() {
            return this.toJSONString();
        }

        @Override
        public String toJSONString() {
            return "{" + StringUtils.jsonQuote( this.key.toString() ) + ":" + JSON.stringify( this.value ) + "}";
        }

        @Override
        public TypeIndex prototype() {
            return Prototype.typeid( this );
        }
    }

    protected class ListEntrySet extends AbstractSet<Map.Entry<Integer, Object > > {
        public final int size()                 { return JSONArraytron.this.size(); }

        public final void clear()               { JSONArraytron.this.clear(); }

        public final Iterator<Map.Entry<Integer, Object > > iterator() {
            return new ListEntryIterator();
        }

        public final boolean contains( Object o ) {
            if ( !(o instanceof Map.Entry) ) {
                return false;
            }
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();

            Object v = JSONArraytron.this.get(key);
            return v != null && v.equals(e.getValue());
        }

        public final boolean remove( Object o ) {
            if ( this.contains(o) ) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();

                return JSONArraytron.this.remove(key) ;
            }
            return false;
        }

        public final Spliterator<Map.Entry<Integer, Object > > spliterator() {
            return Spliterators.spliterator( this, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.DISTINCT );
        }
    }


    protected abstract class JAListEntryIterator {
        protected Iterator<Object >        currentIterator;
        protected int                      index;
        protected JSONArrayEntry           dummyEntry;

        public JAListEntryIterator() {
            this.index            = 0;
            this.currentIterator  = JSONArraytron.this.iterator();
            this.dummyEntry       = new JSONArrayEntry( this.index, null );
        }

        public boolean hasNext() {
            return this.currentIterator.hasNext();
        }

        protected Map.Entry<Integer, Object > nextNode() {
            if ( !this.hasNext() ) {
                throw new NoSuchElementException();
            }

            this.dummyEntry.setKey( this.index++ );
            this.dummyEntry.setValue( this.currentIterator.next() );
            return this.dummyEntry;
        }

        public void remove() {
            this.currentIterator.remove();
        }
    }

    protected final class ListKeyIterator extends JAListEntryIterator implements Iterator<Integer > {
        public final Integer next() { return nextNode().getKey(); }
    }

    protected final class ListValueIterator extends JAListEntryIterator implements Iterator<Object > {
        public final Object next() { return nextNode().getValue(); }
    }

    protected final class ListEntryIterator extends JAListEntryIterator implements Iterator<Map.Entry<Integer,Object > > {
        public final Map.Entry<Integer, Object > next() { return nextNode(); }
    }
}
