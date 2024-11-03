package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.Prototype;
import com.pinecone.framework.system.prototype.TypeIndex;
import com.pinecone.framework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.UUID;
import java.util.Iterator;
import java.util.List;

public abstract class ArchJSONObject implements JSONObject, Serializable {
    protected abstract void jsonDecode0( ArchCursorParser x ) throws JSONException ;

    @Override
    public abstract JSONObject jsonDecode( ArchCursorParser x ) throws JSONException ;

    @Override
    public abstract JSONObject jsonDecode( String source ) throws JSONException;


    @Override
    public abstract JSONObject assimilate( Map<String, Object> that );



    @Override
    public JSONObject shareFrom( JSONObject that, String szKey ) {
        this.put( szKey, that.get( szKey ) );
        return this;
    }

    @Override
    public JSONObject shareFrom( JSONObject that, String[] szKeys ) {
        for ( String szKey : szKeys ) {
            this.putOnce( szKey, that.get( szKey ) );
        }
        return this;
    }

    @Override
    public JSONObject subJson ( String szKey ) {
        JSONObject that = new JSONMaptron();
        that.shareFrom( this, szKey );
        return that;
    }

    @Override
    public JSONObject subJson ( String[] szKeys ) {
        JSONObject that = new JSONMaptron();
        that.shareFrom( this, szKeys );
        return that;
    }

    @Override
    public JSONObject detachSub ( String szKey ) {
        JSONObject that = new JSONMaptron();
        that.put( szKey, this.get( szKey ) );
        this.remove( szKey );
        return that;
    }

    @Override
    public JSONObject detachSub ( String[] szKeys ) {
        JSONObject that = new JSONMaptron();
        for ( String szKey : szKeys ) {
            that.putOnce( szKey, this.get( szKey ) );
            this.remove( szKey );
        }
        return that;
    }

    @Override
    public JSONObject moveSubFrom ( JSONObject that, String szKey ) {
        this.put( szKey, that.get( szKey ) );
        that.remove( szKey );
        return this;
    }

    @Override
    public JSONObject moveSubFrom ( JSONObject that, String[] szKeys ) {
        for ( String szKey : szKeys ) {
            this.putOnce( szKey, that.get( szKey ) );
            that.remove( szKey );
        }
        return this;
    }



    @Override
    public abstract Map<String, Object > getMap();

    /** Basic Map **/
    @Override
    public abstract int size();

    @Override
    public abstract boolean isEmpty();

    protected abstract boolean innerMapContainsKey( Object key );

    @Override
    public boolean containsKey( Object key ) {
        boolean result = this.innerMapContainsKey( key );
        if ( !result && ( key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID ) ) {
            result = this.innerMapContainsKey( key.toString() );
        }

        return result;
    }

    @Override
    public abstract boolean containsValue( Object value );

    @Override
    public abstract void putAll( Map<? extends String, ?> m );

    public JSONObject xPutAll(Map<? extends String, ?> m ) {
        this.putAll(m);
        return this;
    }

    @Override
    public abstract void clear();

    @Override
    public JSONObject xClear() {
        this.clear();
        return this;
    }

    @Override
    public abstract Object remove( Object key );

    @Override
    public Object erase( Object key ) {
        return this.remove( key );
    }

    @Override
    public JSONObject xRemove(Object key) {
        this.remove(key);
        return this;
    }

    @Override
    public abstract Set<String > keySet();

    @Override
    public abstract Collection<Object > values();

    @Override
    public abstract Set<Map.Entry<String, Object > > entrySet();


    @Override
    public JSONObject accumulate( String key, Object value ) throws JSONException {
        JSONUtils.prospectNumberQualify(value);
        Object object = this.opt(key);
        if ( object == null ) {
            this.put( key, value instanceof JSONArray ? (new JSONArraytron()).put(value) : value );
        }
        else if ( object instanceof JSONArray ) {
            ((JSONArray)object).put(value);
        }
        else {
            this.put(key, (Object)( new JSONArraytron()).put(object).put(value) );
        }

        return this;
    }

    @Override
    public JSONObject append( String key, Object value ) throws JSONException {
        JSONUtils.prospectNumberQualify(value);
        Object object = this.opt(key);
        if ( object == null ) {
            this.put(key, (Object)( new JSONArraytron() ).put(value));
        }
        else {
            if ( !(object instanceof JSONArray) ) {
                throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
            }

            this.put(key, (Object)((JSONArray)object).put(value));
        }

        return this;
    }


    protected abstract Object innerMapGet( Object key ) ;

    @Override
    public Object get( Object key ) {
        Object val = this.innerMapGet(key);
        if ( val == null && ( key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID ) ) {
            val = this.innerMapGet( key.toString() );
        }

        return val;
    }

    @Override
    public Object get( String key ) throws JSONException {
        if ( key == null ) {
            throw new JSONException("Null key.");
        }
        else {
            Object object = this.opt(key);
            if (object == null) {
                throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] not found.");
            }
            else {
                return object;
            }
        }
    }

    @Override
    public boolean getBoolean( String key ) throws JSONException {
        Object object = this.get(key);
        if (!object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false"))) {
            if (!object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true"))) {
                throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] is not a Boolean.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public double getDouble( String key ) throws JSONException {
        Object object = this.get(key);

        try {
            return object instanceof Number ? ((Number)object).doubleValue() : Double.parseDouble((String)object);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] is not a number.");
        }
    }

    @Override
    public int getInt( String key ) throws JSONException {
        Object object = this.get(key);

        try {
            return object instanceof Number ? ((Number)object).intValue() : Integer.parseInt((String)object);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] is not an int.");
        }
    }

    @Override
    public JSONArray getJSONArray  ( String key ) throws JSONException {
        Object object = this.get(key);
        if ( object instanceof JSONArray ) {
            return (JSONArray)object;
        }
        else {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] is not a JSONArray.");
        }
    }

    @Override
    public JSONObject getJSONObject( String key ) throws JSONException {
        Object object = this.get(key);
        if ( object instanceof JSONObject ) {
            return (JSONObject)object;
        }
        else {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] is not a JSONObject.");
        }
    }

    @Override
    public long getLong( String key ) throws JSONException {
        Object object = this.get(key);

        try {
            return object instanceof Number ? ((Number)object).longValue() : Long.parseLong((String)object);
        }
        catch (Exception e) {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] is not a long.");
        }
    }

    @Override
    public String getString( String key ) throws JSONException {
        Object object = this.get(key);
        if ( object instanceof String ) {
            return (String)object;
        }
        else {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] not a string.");
        }
    }

    @Override
    public byte[] getBytes( String key ) throws JSONException {
        Object object = this.get(key);
        if ( object instanceof String ) {
            return ( (String)object ).getBytes();
        }
        else if ( object instanceof byte[] ) {
            return (byte[])( (byte[])object );
        }
        else {
            throw new JSONException("JSONObject[" + StringUtils.jsonQuote(key) + "] not a string nor bytes.");
        }
    }

    @Override
    public JSONArray affirmArray( String key ) {
        Object o = this.opt(key);
        if( o instanceof JSONArray ){
            return (JSONArray)o;
        }
        JSONArray jNew = new JSONArraytron();
        this.put( key, jNew );
        return jNew;
    }

    @Override
    public JSONObject affirmObject(String key ) {
        Object o = this.opt( key );
        if( o instanceof JSONObject ){
            return (JSONObject) o;
        }
        JSONObject jNew = new JSONMaptron();
        this.put( key, jNew );
        return jNew;
    }

    @Override
    public Object affirm( String key ) {
        if( this.containsKey( key ) ){
            return this.opt(key);
        }

        Object o = JSON.NULL;
        this.put( key, o );
        return o;
    }

    @Override
    public Object opt( String key ) {
        return key == null ? null : this.innerMapGet( key );
    }

    @Override
    public boolean optBoolean( String key ) {
        return this.optBoolean(key, false);
    }

    @Override
    public boolean optBoolean( String key, boolean defaultValue ) {
        try {
            return this.getBoolean(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public double optDouble( String key ) {
        return this.optDouble( key, Double.NaN );
    }

    @Override
    public double optDouble( String key, double defaultValue ) {
        try {
            return this.getDouble(key);
        }
        catch ( Exception e ) {
            return defaultValue;
        }
    }

    @Override
    public int optInt( String key ) {
        return this.optInt(key, 0);
    }

    @Override
    public int optInt( String key, int defaultValue ) {
        try {
            return this.getInt(key);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public JSONArray optJSONArray( String key) {
        Object o = this.opt(key);
        return o instanceof JSONArray ? (JSONArray)o : null;
    }

    @Override
    public JSONObject optJSONObject( String key) {
        Object object = this.opt(key);
        return object instanceof JSONObject ? (JSONObject)object : null;
    }

    @Override
    public long optLong( String key ) {
        return this.optLong(key, 0L);
    }

    @Override
    public long optLong( String key, long defaultValue ) {
        try {
            return this.getLong(key);
        }
        catch ( Exception e ) {
            return defaultValue;
        }
    }

    @Override
    public String optString( String key ) {
        return this.optString(key, "");
    }

    @Override
    public String optString( String key, String defaultValue ) {
        Object object = this.opt(key);
        return JSON.NULL.equals(object) ? defaultValue : object.toString();
    }

    @Override
    public byte[] optBytes( String key ) {
        return this.optBytes( key, "".getBytes() );
    }

    @Override
    public byte[] optBytes( String key, byte[] defaultValue ) {
        try {
            return this.getBytes( key );
        }
        catch ( Exception e ) {
            return defaultValue;
        }
    }

    @Override
    public Object opt( Object key ) {
        try{
            return this.opt( JSONUtils.asStringKey( key ) );
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public boolean optBoolean( Object key ) {
        try {
            return this.optBoolean(JSONUtils.asStringKey(key));
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public double optDouble( Object key ) {
        try {
            return this.optDouble(JSONUtils.asStringKey(key));
        }
        catch ( Exception e ) {
            return Double.NaN;
        }
    }

    @Override
    public int optInt( Object key ) {
        try {
            return this.optInt(JSONUtils.asStringKey(key));
        }
        catch ( Exception e ) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public JSONArray optJSONArray( Object key ) {
        try {
            return this.optJSONArray( JSONUtils.asStringKey(key) );
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public JSONObject optJSONObject( Object key ) {
        try {
            return this.optJSONObject( JSONUtils.asStringKey(key) );
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public long optLong( Object key ) {
        try {
            return this.optLong(JSONUtils.asStringKey(key));
        }
        catch ( Exception e ) {
            return Long.MAX_VALUE;
        }
    }

    @Override
    public String optString( Object key ) {
        try {
            return this.optString(JSONUtils.asStringKey(key));
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public byte[] optBytes( Object key ) {
        try {
            return this.optBytes(JSONUtils.asStringKey(key));
        }
        catch (Exception e) {
            return null;
        }
    }




    @Override
    public JSONObject increment( String key ) throws JSONException {
        Object value = this.opt(key);
        if (value == null) {
            this.put(key, 1);
        }
        else if (value instanceof Integer) {
            this.put(key, (Integer)value + 1);
        }
        else if (value instanceof Long) {
            this.put(key, (Long)value + 1L);
        }
        else if (value instanceof Double) {
            this.put(key, (Double)value + 1.0D);
        }
        else {
            if (!(value instanceof Float)) {
                throw new JSONException("Unable to increment [" + StringUtils.jsonQuote(key) + "].");
            }

            this.put(key, (double)((Float)value + 1.0F));
        }

        return this;
    }

    @Override
    public boolean isNull( String key ) {
        return JSON.NULL.equals(this.opt(key));
    }

    @Override
    public Iterator keys() {
        return this.keySet().iterator();
    }

    @Override
    public JSONArray names() {
        JSONArray ja = new JSONArraytron();
        Iterator keys = this.keys();

        while( keys.hasNext() ) {
            ja.put( keys.next() );
        }

        return ja.length() == 0 ? null : ja;
    }

    @Override
    public String[] getOwnPropertyNames () {
        return JSONUtils.getOwnPropertyNames( this );
    }


    protected abstract Object innerMapPut( String key, Object value );

    @Override
    public JSONObject insert( Object key, Object value ) {
        return this.put( key.toString(), value );
    }

    @Override
    public Object putIfAbsent( String key, Object value ) {
        return this.getMap().putIfAbsent( key, value );
    }

    @Override
    public Object insertIfAbsent( Object key, Object value ) {
        return this.putIfAbsent( key.toString(), value );
    }



    @Override
    public JSONObject put( String key, boolean value ) throws JSONException {
        this.put(key, (Object)(value ? Boolean.TRUE : Boolean.FALSE));
        return this;
    }

    @Override
    public JSONObject put( String key, Collection value ) throws JSONException {
        this.put(key, (Object)(new JSONArraytron(value)));
        return this;
    }

    @Override
    public JSONObject put( String key, double value ) throws JSONException {
        this.put( key, (Double) value );
        return this;
    }

    @Override
    public JSONObject put( String key, int value ) throws JSONException {
        this.put( key, (Integer)value );
        return this;
    }

    @Override
    public JSONObject put( String key, long value ) throws JSONException {
        this.put(key, (Object) (Long) value );
        return this;
    }

    @Override
    public abstract JSONObject put( String key, Map value ) throws JSONException ;

    @Override
    public JSONObject put( String key, JSONArray value ) throws JSONException {
        this.innerMapPut( key, value );
        return this;
    }

    @Override
    public JSONObject put( String key, JSONObject value ) throws JSONException {
        this.innerMapPut( key, value );
        return this;
    }

    @Override
    public JSONObject put( String key, Object value ) throws JSONException {
        if ( key == null ) {
            throw new NullPointerException( "Null key." );
        }
        else {
            if ( value != null ) {
                JSONUtils.prospectNumberQualify( value );
                this.innerMapPut( key, value );
            }
            else {
                this.remove( key );
            }
            return this;
        }
    }

    @Override
    public JSONObject embed( String key, Object value ) throws JSONException {
        if ( key == null ) {
            throw new NullPointerException("Null key.");
        }
        else {
            if ( value != null ) {
                JSONUtils.prospectNumberQualify(value);
                this.innerMapPut( key, value );
            }
            else {
                this.innerMapPut( key, JSON.NULL );
            }
            return this;
        }
    }

    @Override
    public JSONObject putOnce( String key, Object value ) throws JSONException {
        if ( key != null && value != null ) {
            if ( this.opt(key) != null ) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }

            this.put(key, value);
        }

        return this;
    }

    @Override
    public JSONObject putOpt( String key, Object value ) throws JSONException {
        if (key != null && value != null) {
            this.put(key, value);
        }

        return this;
    }


    protected abstract Object innerMapRemove( String key );

    @Override
    public Object remove( String key ) {
        return this.innerMapRemove(key);
    }

    @Override
    public JSONObject removeAll( Collection<String > keys ) {
        for( String key : keys ) {
            this.remove( key );
        }
        return this;
    }

    @Override
    public JSONObject removeAll( String[] keys ) {
        for( String key : keys ) {
            this.remove( key );
        }
        return this;
    }

    @Override
    public JSONArray toJSONArray( JSONArray names ) throws JSONException {
        if (names != null && names.length() != 0) {
            JSONArray ja = new JSONArraytron();

            for( int i = 0; i < names.length(); ++i ) {
                ja.put(this.opt(names.getString(i)));
            }

            return ja;
        } else {
            return null;
        }
    }

    @Override
    public JSONArray toJSONArray() {
        JSONArray jRegressed = new JSONArraytron();

        for ( Object obj : this.entrySet() ) {
            Map.Entry kv = ( Map.Entry ) obj;
            jRegressed.put( kv.getValue() );
        }

        return jRegressed;
    }

    @Override
    public JSONObject toJSONObject() {
        return this;
    }

    @Override
    public abstract Map.Entry<String, Object > front() ;

    @Override
    public abstract Map.Entry<String, Object > back() ;



    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        try {
            return this.toJSONString( 0 );
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public String toJSONStringI( int nIndentFactor ) {
        try {
            return this.toJSONString(nIndentFactor);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toJSONString( int nIndentFactor ) throws IOException {
        StringWriter w = new StringWriter();
        synchronized( w.getBuffer() ) {
            return this.write( w, nIndentFactor,0 ).toString();
        }
    }

    @Override
    public TypeIndex prototype() {
        return Prototype.typeid( this );
    }

    @Override
    public String prototypeName() {
        return Prototype.prototypeName( this );
    }

    @Override
    public boolean isPrototypeOf  ( TypeIndex that ) {
        return that.equals( this.prototype() );
    }

    @Override
    public boolean hasOwnProperty ( Object key ) {
        return this.containsKey( key );
    }

    @Override
    public Map<?, Object > toMap(){
        return this;
    }

    @Override
    public List<Object > toList(){
        return this.toJSONArray();
    }


    @Override
    public JSONObject clone() {
        try {
            return (JSONObject) super.clone();
        }
        catch ( CloneNotSupportedException e ) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    @Override
    public Writer write( Writer writer ) throws IOException {
        return this.write( writer, 0, 0 );
    }

    @Override
    public Writer write( Writer writer, int nIndentFactor ) throws IOException {
        return this.write( writer, nIndentFactor, 0 );
    }

    @Override
    public abstract Writer write( Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException ;
}