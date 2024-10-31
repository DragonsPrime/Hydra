package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.PineUnit;
import com.pinecone.framework.system.prototype.TypeIndex;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;

public interface JSONArray extends PineUnit, List<Object >, JSONDictium, Serializable, RandomAccess, Cloneable {
    JSONArray jsonDecode( ArchCursorParser x ) throws JSONException ;

    JSONArray jsonDecode( String source ) throws JSONException ;

    void assimilate( List<Object > that );

    List<Object > getArray();



    Object front() ;

    Object back() ;

    int length() ;

    /** Basic List<Object> **/
    @Override
    int size() ;

    @Override
    boolean isEmpty() ;

    @Override
    boolean contains( Object o ) ;

    @Override
    Iterator<Object> iterator() ;

    @Override
    Object[] toArray() ;

    @Override
    <T> T[] toArray( T[] a ) ;

    @Override
    boolean add( Object e ) ;

    @Override
    void clear() ;

    @Override
    Object remove( int index ) ;

    JSONArray xRemove( int index ) ;

    @Override
    boolean remove( Object o ) ;

    JSONArray xRemove(Object o) ;

    @Override
    Object erase( Object key ) ;

    @Override
    boolean containsAll( Collection<?> c ) ;

    @Override
    boolean addAll( Collection<?> c ) ;

    JSONArray xAddAll( Collection<?> c ) ;

    @Override
    boolean addAll( int index, Collection<?> c ) ;

    JSONArray xAddAll( int index, Collection<?> c ) ;

    @Override
    boolean removeAll( Collection<?> c ) ;

    JSONArray xRemoveAll( Collection<?> c ) ;

    @Override
    boolean retainAll( Collection<?> c ) ;

    JSONArray xRetainAll(Collection<?> c) ;

    @Override
    Object set( int index, Object element ) ;

    JSONArray xSet( int index, Object element ) ;

    Object affirm( int index ) ;

    JSONObject affirmObject( int index ) ;

    JSONArray affirmArray( int index ) ;

    @Override
    boolean containsValue( Object value ) ;



    @Override
    void add( int index, Object element ) ;

    JSONArray xAdd( int index, Object element ) ;

    @Override
    int indexOf( Object o ) ;

    @Override
    int lastIndexOf( Object o ) ;

    @Override
    ListIterator<Object> listIterator() ;

    @Override
    ListIterator<Object> listIterator(int index) ;

    @Override
    List<Object> subList( int fromIndex, int toIndex ) ;



    @Override
    Object get( int index ) throws JSONException ;

    @Override
    Object get( Object key ) ;

    boolean getBoolean( int index ) throws JSONException ;

    double getDouble( int index ) throws JSONException ;

    int getInt( int index ) throws JSONException ;

    JSONArray getJSONArray( int index ) throws JSONException ;

    JSONObject getJSONObject( int index ) throws JSONException ;

    long getLong( int index ) throws JSONException ;

    String getString( int index ) throws JSONException ;

    byte[] getBytes( int index ) throws JSONException ;

    boolean isNull( int index ) ;

    String join( String separator ) throws JSONException ;

    Object opt( int index ) ;

    boolean optBoolean( int index ) ;

    boolean optBoolean(int index, boolean defaultValue) ;

    double optDouble( int index ) ;

    double optDouble( int index, double defaultValue ) ;

    int optInt( int index ) ;

    int optInt( int index, int defaultValue ) ;

    JSONArray optJSONArray( int index ) ;

    JSONObject optJSONObject( int index ) ;

    long optLong( int index ) ;

    long optLong( int index, long defaultValue ) ;

    String optString( int index ) ;

    String optString( int index, String defaultValue ) ;

    byte[] optBytes( int index ) ;

    byte[] optBytes( int index, byte[] defaultValue ) ;


    @Override
    Object opt( Object key ) ;

    @Override
    boolean optBoolean( Object key ) ;

    @Override
    double optDouble( Object key ) ;

    @Override
    int optInt( Object key ) ;

    @Override
    JSONArray optJSONArray( Object key ) ;

    @Override
    JSONObject optJSONObject( Object key ) ;

    @Override
    long optLong( Object key ) ;

    @Override
    String optString( Object key ) ;

    @Override
    byte[] optBytes( Object key ) ;








    @Override
    JSONArray insert( Object key, Object val ) ;

    JSONArray put( boolean value ) ;

    JSONArray put( Collection value ) ;

    JSONArray put( double value ) throws JSONException ;

    JSONArray put( int value ) ;

    JSONArray put( long value ) ;

    JSONArray put( Map value ) ;

    JSONArray put( Object value ) ;

    JSONArray put( JSONObject value ) ;

    JSONArray put( JSONArray value ) ;

    JSONArray put( int index, boolean value ) throws JSONException ;

    JSONArray put( int index, Collection value ) throws JSONException ;

    JSONArray put( int index, double value ) throws JSONException ;

    JSONArray put( int index, int value ) throws JSONException ;

    JSONArray put( int index, long value ) throws JSONException ;

    JSONArray put( int index, Map value ) throws JSONException ;

    JSONArray put( int index, Object value ) throws JSONException ;

    JSONArray put( int index, JSONObject value ) throws JSONException ;

    JSONArray put( int index, JSONArray value ) throws JSONException ;

    JSONObject toJSONObject( JSONArray names ) throws JSONException ;

    @Override
    JSONObject toJSONObject()  ;

    @Override
    JSONArray toJSONArray()  ;

    @Override
    Set<?> entrySet() ;

    @Override
    Collection<Object > values() ;

    @Override
    Map<?, Object > toMap() ;

    @Override
    List<Object > toList() ;







    @Override
    boolean hasOwnProperty( Object elm ) ;

    boolean hasOwnProperty( int elm ) ;

    @Override
    boolean containsKey( Object elm ) ;

    boolean containsKey( int elm ) ;



    @Override
    String toJSONString() ;

    String toJSONStringI( int nIndentFactor ) ;

    String toJSONString( int nIndentFactor ) throws IOException ;

    @Override
    TypeIndex prototype() ;

    @Override
    String prototypeName() ;

    @Override
    boolean isPrototypeOf( TypeIndex that ) ;


    JSONArray clone() ;

    Writer write(Writer writer) throws IOException ;

    Writer write( Writer writer, int nIndentFactor ) throws IOException ;

    Writer write( Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException ;

}
