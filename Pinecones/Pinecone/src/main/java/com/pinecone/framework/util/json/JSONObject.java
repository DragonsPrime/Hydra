package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.PineUnit;
import com.pinecone.framework.system.prototype.TypeIndex;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;

public interface JSONObject extends PineUnit, Map<String, Object>, JSONDictium, Cloneable, Serializable {
    JSONObject jsonDecode( ArchCursorParser x ) throws JSONException ;

    JSONObject jsonDecode( String source ) throws JSONException ;

    JSONObject assimilate( Map<String, Object> that );




    JSONObject shareFrom( JSONObject that, String szKey ) ;

    JSONObject shareFrom( JSONObject that, String[] szKeys ) ;

    JSONObject subJson ( String szKey ) ;

    JSONObject subJson ( String[] szKeys ) ;

    JSONObject detachSub ( String szKey ) ;

    JSONObject detachSub ( String[] szKeys ) ;

    JSONObject moveSubFrom ( JSONObject that, String szKey ) ;

    JSONObject moveSubFrom ( JSONObject that, String[] szKeys ) ;



    /**
     * 20240625
     * Eliminates all keys excepted the survivor key.
     * @param szSurvivorKey The `key` the can surviving.
     * @return this
     */
    default JSONObject eliminateExcepts( String szSurvivorKey ) {
        Object sub = this.opt( szSurvivorKey );
        this.clear();
        this.embed( szSurvivorKey, sub );
        return this;
    }

    /**
     * 20240625
     * Eliminates all keys excepted survivor keys.
     * @param szSurvivorKeys The batch of `keys` that can surviving.
     * @return this
     */
    default JSONObject eliminateExcepts( String[] szSurvivorKeys ) {
        JSONObject sub = this.subJson( szSurvivorKeys );
        this.clear();
        this.assimilate( sub.getMap() );
        return this;
    }




    Map<String, Object > getMap();

    /** Basic Map **/
    @Override
    int size() ;

    @Override
    boolean isEmpty() ;

    @Override
    boolean containsKey( Object key ) ;

    @Override
    boolean containsValue( Object value ) ;

    @Override
    void putAll( Map<? extends String, ?> m ) ;

    JSONObject xPutAll( Map<? extends String, ?> m ) ;

    @Override
    void clear() ;

    JSONObject xClear() ;

    @Override
    Object remove( Object key ) ;

    @Override
    Object erase( Object key ) ;

    JSONObject xRemove(Object key) ;

    @Override
    Set<String > keySet() ;

    @Override
    Collection<Object > values() ;

    @Override
    Set<Map.Entry<String, Object > > entrySet() ;


    JSONObject accumulate( String key, Object value ) throws JSONException ;

    JSONObject append( String key, Object value ) throws JSONException ;







    @Override
    Object get( Object key ) ;

    Object get( String key ) throws JSONException ;

    boolean getBoolean( String key ) throws JSONException ;

    double getDouble( String key ) throws JSONException ;

    int getInt( String key ) throws JSONException ;

    JSONArray getJSONArray  ( String key ) throws JSONException ;

    JSONObject getJSONObject( String key ) throws JSONException ;

    long getLong( String key ) throws JSONException ;

    String getString( String key ) throws JSONException ;

    byte[] getBytes( String key ) throws JSONException ;

    JSONArray affirmArray( String key ) ;

    JSONObject affirmObject( String key ) ;

    Object affirm( String key ) ;

    Object opt( String key ) ;

    boolean optBoolean( String key ) ;

    boolean optBoolean( String key, boolean defaultValue ) ;

    double optDouble( String key ) ;

    double optDouble( String key, double defaultValue ) ;

    int optInt( String key ) ;

    int optInt( String key, int defaultValue ) ;

    JSONArray optJSONArray( String key) ;

    JSONObject optJSONObject( String key) ;

    long optLong( String key ) ;

    long optLong( String key, long defaultValue ) ;

    String optString( String key ) ;

    String optString( String key, String defaultValue ) ;

    byte[] optBytes( String key ) ;

    byte[] optBytes( String key, byte[] defaultValue ) ;

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




    JSONObject increment( String key ) throws JSONException ;

    boolean isNull( String key ) ;

    Iterator keys() ;

    JSONArray names() ;

    String[] getOwnPropertyNames () ;







    @Override
    JSONObject insert( Object key, Object value ) ;

    JSONObject put( String key, boolean value ) throws JSONException ;

    JSONObject put( String key, Collection value ) throws JSONException ;

    JSONObject put( String key, double value ) throws JSONException ;

    JSONObject put( String key, int value ) throws JSONException ;

    JSONObject put( String key, long value ) throws JSONException ;

    JSONObject put( String key, Map value ) throws JSONException ;

    JSONObject put( String key, JSONArray value ) throws JSONException ;

    JSONObject put( String key, JSONObject value ) throws JSONException ;

    @Override
    JSONObject put( String key, Object value ) throws JSONException ;

    JSONObject embed( String key, Object value ) throws JSONException ;

    JSONObject putOnce( String key, Object value ) throws JSONException ;

    JSONObject putOpt( String key, Object value ) throws JSONException ;







    Object remove( String key ) ;

    JSONObject removeAll( Collection<String > keys );

    JSONObject removeAll( String[] keys );

    JSONArray toJSONArray( JSONArray names ) throws JSONException ;

    @Override
    JSONArray toJSONArray() ;

    @Override
    JSONObject toJSONObject() ;

    Map.Entry<String, Object > front() ;

    Map.Entry<String, Object > back() ;



    @Override
    String toJSONString() ;

    String toJSONStringI( int nIndentFactor ) ;

    String toJSONString( int nIndentFactor ) throws IOException ;

    @Override
    TypeIndex prototype() ;

    @Override
    String prototypeName() ;

    @Override
    boolean isPrototypeOf  ( TypeIndex that ) ;

    @Override
    boolean hasOwnProperty ( Object key ) ;

    @Override
    Map<?, Object > toMap();

    @Override
    List<Object > toList();



    JSONObject clone() ;

    Writer write(Writer writer ) throws IOException ;

    Writer write( Writer writer, int nIndentFactor ) throws IOException ;

    Writer write( Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException ;
}
