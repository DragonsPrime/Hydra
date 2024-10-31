package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.Prototype;
import com.pinecone.framework.system.prototype.TypeIndex;
import com.pinecone.framework.util.Debug;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.ListIterator;


public abstract class ArchJSONArray implements JSONArray {
    protected abstract void jsonDecode0( ArchCursorParser x ) throws JSONException ;

    @Override
    public abstract JSONArray jsonDecode( ArchCursorParser x ) throws JSONException ;

    @Override
    public abstract JSONArray jsonDecode( String source ) throws JSONException ;

    @Override
    public abstract void assimilate( List<Object > that );

    @Override
    public abstract List<Object > getArray();

    @Override
    public Object front() {
        return this.opt( 0 );
    }

    @Override
    public Object back() {
        return this.opt( this.length() - 1 );
    }

    @Override
    public int length() {
        return this.size();
    }

    /** Basic List<Object> **/
    @Override
    public abstract int size();

    @Override
    public abstract boolean isEmpty();

    @Override
    public abstract boolean contains( Object o );

    @Override
    public abstract Iterator<Object> iterator();

    @Override
    public abstract Object[] toArray();

    @Override
    public abstract <T> T[] toArray( T[] a ) ;





    protected abstract boolean innerListAdd( Object e );

    @Override
    public boolean add( Object e ) {
        return this.innerListAdd(e);
    }

    @Override
    public abstract void clear();



    protected abstract boolean innerListRemove( Object index );

    @Override
    public Object remove( int index ) {
        Object o = this.opt(index);
        if ( index >= 0 && index < this.length() ) {
            this.innerListRemove( index );
        }

        return o;
    }

    @Override
    public JSONArray xRemove( int index ) {
        this.remove(index);
        return this;
    }

    @Override
    public boolean remove( Object o ) {
        return this.innerListRemove( o );
    }

    @Override
    public JSONArray xRemove( Object o ) {
        this.remove(o);
        return this;
    }

    @Override
    public Object erase( Object key ) {
        return this.remove( JSONUtils.asInt32Key( key ) );
    }

    @Override
    public abstract boolean containsAll( Collection<?> c );

    @Override
    public abstract boolean addAll( Collection<?> c );

    @Override
    public JSONArray xAddAll( Collection<?> c ) {
        this.addAll(c);
        return this;
    }

    @Override
    public abstract boolean addAll( int index, Collection<?> c );

    @Override
    public JSONArray xAddAll( int index, Collection<?> c ) {
        this.addAll( index, c );
        return this;
    }

    @Override
    public abstract boolean removeAll( Collection<?> c );

    @Override
    public JSONArray xRemoveAll( Collection<?> c ) {
        this.removeAll(c);
        return this;
    }

    @Override
    public abstract boolean retainAll( Collection<?> c );

    @Override
    public JSONArray xRetainAll(Collection<?> c) {
        this.retainAll(c);
        return this;
    }

    protected void affirmCapacity( int cap ) {
        for( int i = this.size(); i < cap; ++i ) {
            this.innerListAdd( JSON.NULL );
        }
    }

    @Override
    public Object set( int index, Object element ) {
        if ( index == -1 ) {
            this.innerListAdd( element );
            return null;
        }
        else if ( this.size() > index ) {
            return this.innerListSet( index, element );
        }
        else {
            this.affirmCapacity( index );
            this.innerListAdd( element );
            return null;
        }
    }

    @Override
    public JSONArray xSet( int index, Object element ) {
        this.set( index, element );
        return this;
    }

    @Override
    public Object affirm( int index ) {
        if ( index == -1 ) {
            this.innerListAdd( JSON.NULL );
            return JSON.NULL;
        }
        else if ( this.size() > index ) {
            return this.innerListGet( index );
        }
        else {
            this.affirmCapacity( index + 1 );
            return this.innerListGet( index );
        }
    }

    @Override
    public JSONObject affirmObject( int index ) {
        if ( index == -1 ) {
            JSONObject obj = new JSONMaptron();
            this.innerListAdd( obj );
            return obj;
        }
        else if ( this.size() > index ) {
            Object obj = this.innerListGet( index );
            if( obj instanceof JSONObject ) {
                return (JSONObject) obj;
            }
            obj = new JSONMaptron();
            this.innerListSet( index, obj );
            return (JSONObject)obj;
        }
        else {
            this.affirmCapacity( index );
            JSONObject obj = new JSONMaptron();
            this.innerListAdd( obj );
            return obj;
        }
    }

    @Override
    public JSONArray affirmArray(int index ) {
        if ( index == -1 ) {
            JSONArray obj = new JSONArraytron();
            this.innerListAdd( obj );
            return obj;
        }
        else if ( this.size() > index ) {
            Object obj = this.innerListGet( index );
            if( obj instanceof JSONArray ) {
                return (JSONArray) obj;
            }
            obj = new JSONArraytron();
            this.innerListSet( index, obj );
            return (JSONArray) obj;
        }
        else {
            this.affirmCapacity( index );
            JSONArray obj = new JSONArraytron();
            this.innerListAdd( obj );
            return obj;
        }
    }

    @Override
    public boolean containsValue( Object value ) {
        return this.contains( value );
    }



    @Override
    public abstract void add( int index, Object element ) ;

    @Override
    public JSONArray xAdd( int index, Object element ) {
        this.add(index, element);
        return this;
    }

    @Override
    public abstract int indexOf( Object o );

    @Override
    public abstract int lastIndexOf( Object o );

    @Override
    public abstract ListIterator<Object > listIterator();

    @Override
    public abstract ListIterator<Object > listIterator( int index );

    @Override
    public abstract List<Object > subList( int fromIndex, int toIndex ) ;





    protected abstract Object innerListGet( int key );

    @Override
    public Object get( int index ) throws JSONException {
        Object object = this.opt( index );
        if ( object == null ) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        else {
            return object;
        }
    }

    @Override
    public Object get( Object key ) {
        return this.get( JSONUtils.asInt32Key( key ) );
    }

    @Override
    public boolean getBoolean( int index ) throws JSONException {
        Object object = this.get(index);
        if ( !object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false")) ) {
            if ( !object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true")) ) {
                throw new JSONException("JSONArray[" + index + "] is not a boolean.");
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public double getDouble( int index ) throws JSONException {
        Object object = this.get(index);

        try {
            return object instanceof Number ? ( (Number)object ).doubleValue() : Double.parseDouble( (String)object );
        }
        catch ( Exception e ) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    @Override
    public int getInt( int index ) throws JSONException {
        Object object = this.get(index);

        try {
            return object instanceof Number ? ( (Number)object ).intValue() : Integer.parseInt( (String)object );
        }
        catch ( Exception e ) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    @Override
    public JSONArray getJSONArray(int index ) throws JSONException {
        Object object = this.get(index);
        if ( object instanceof JSONArray ) {
            return (JSONArray)object;
        }
        else {
            throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
        }
    }

    @Override
    public JSONObject getJSONObject( int index ) throws JSONException {
        Object object = this.get(index);
        if ( object instanceof JSONObject ) {
            return (JSONObject)object;
        }
        else {
            throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
        }
    }

    @Override
    public long getLong( int index ) throws JSONException {
        Object object = this.get(index);

        try {
            return object instanceof Number ? ( (Number)object ).longValue() : Long.parseLong( (String)object );
        }
        catch ( Exception e ) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    @Override
    public String getString( int index ) throws JSONException {
        Object object = this.get(index);
        if ( object instanceof String ) {
            return (String)object;
        }
        else {
            throw new JSONException("JSONArray[" + index + "] not a string.");
        }
    }

    @Override
    public byte[] getBytes( int index ) throws JSONException {
        Object object = this.get(index);
        if ( object instanceof String ) {
            return ((String) object).getBytes();
        }
        else if ( object instanceof byte[] ) {
            return (byte[])( (byte[])object );
        }
        else {
            throw new JSONException("JSONObject[" + index + "] not a string nor bytes.");
        }
    }

    @Override
    public boolean isNull( int index ) {
        return JSON.NULL.equals(this.opt(index));
    }

    @Override
    public String join( String separator ) throws JSONException {
        int len = this.length();
        StringBuffer sb = new StringBuffer();

        for( int i = 0; i < len; ++i ) {
            if (i > 0) {
                sb.append(separator);
            }

            sb.append( JSONUtils.valueToString( this.innerListGet(i)) );
        }

        return sb.toString();
    }

    @Override
    public Object opt( int index ) {
        return index >= 0 && index < this.length() ? this.innerListGet( index ) : null;
    }

    @Override
    public boolean optBoolean( int index ) {
        return this.optBoolean(index, false);
    }

    @Override
    public boolean optBoolean( int index, boolean defaultValue ) {
        try {
            return this.getBoolean(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public double optDouble( int index ) {
        return this.optDouble( index, Double.NaN );
    }

    @Override
    public double optDouble( int index, double defaultValue ) {
        try {
            return this.getDouble(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public int optInt( int index ) {
        return this.optInt(index, 0);
    }

    @Override
    public int optInt( int index, int defaultValue ) {
        try {
            return this.getInt(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public JSONArray optJSONArray( int index ) {
        Object o = this.opt(index);
        return o instanceof JSONArray ? (JSONArray)o : null;
    }

    @Override
    public JSONObject optJSONObject( int index ) {
        Object o = this.opt(index);
        return o instanceof JSONObject ? (JSONObject)o : null;
    }

    @Override
    public long optLong( int index ) {
        return this.optLong(index, 0L);
    }

    @Override
    public long optLong( int index, long defaultValue ) {
        try {
            return this.getLong(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String optString( int index ) {
        return this.optString(index, "");
    }

    @Override
    public String optString( int index, String defaultValue ) {
        Object object = this.opt(index);
        return JSON.NULL.equals(object) ? defaultValue : object.toString();
    }

    @Override
    public byte[] optBytes( int index ) {
        return this.optBytes( index, "".getBytes() );
    }

    @Override
    public byte[] optBytes( int index, byte[] defaultValue ) {
        try {
            return this.getBytes( index );
        }
        catch ( Exception e ) {
            return defaultValue;
        }
    }


    @Override
    public Object opt( Object key ) {
        try {
            return this.opt(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public boolean optBoolean( Object key ) {
        try {
            return this.optBoolean(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return false;
        }
    }

    @Override
    public double optDouble( Object key ) {
        try {
            return this.optDouble(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return Double.NaN;
        }
    }

    @Override
    public int optInt( Object key ) {
        try {
            return this.optInt(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public JSONArray optJSONArray( Object key ) {
        try {
            return this.optJSONArray(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public JSONObject optJSONObject( Object key ) {
        try {
            return this.optJSONObject(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public long optLong( Object key ) {
        try {
            return this.optLong(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return Long.MAX_VALUE;
        }
    }

    @Override
    public String optString( Object key ) {
        try {
            return this.optString(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return null;
        }
    }

    @Override
    public byte[] optBytes( Object key ) {
        try {
            return this.optBytes(JSONUtils.asInt32Key(key));
        }
        catch ( Exception e ) {
            return null;
        }
    }





    protected abstract Object innerListSet( int index, Object element );

    @Override
    public JSONArray insert( Object key, Object val ) {
        return this.put( JSONUtils.asInt32Key( key ), val );
    }

    @Override
    public Object insertIfAbsent( Object key, Object value ) {
        if( !this.containsKey( JSONUtils.asInt32Key( key ) ) ){
            return this.insert( key, value );
        }
        return null;
    }

    @Override
    public JSONArray put( boolean value ) {
        this.put((Object)(value ? Boolean.TRUE : Boolean.FALSE));
        return this;
    }

    @Override
    public abstract JSONArray put( Collection value );

    @Override
    public JSONArray put( double value ) throws JSONException {
        Double d = value;
        JSONUtils.prospectNumberQualify(d);
        this.put( (Object)d );
        return this;
    }

    @Override
    public JSONArray put( int value ) {
        this.put( (Integer)value );
        return this;
    }

    @Override
    public JSONArray put( long value ) {
        this.put( (Long)value );
        return this;
    }

    @Override
    public JSONArray put( Map value ) {
        this.put((Object)(new JSONMaptron(value)));
        return this;
    }

    @Override
    public JSONArray put( Object value ) {
        this.innerListAdd( value );
        return this;
    }

    @Override
    public JSONArray put( JSONObject value ) {
        this.innerListAdd( value );
        return this;
    }

    @Override
    public JSONArray put( JSONArray value ) {
        this.innerListAdd( value );
        return this;
    }

    @Override
    public JSONArray put( int index, boolean value ) throws JSONException {
        this.put( index, (Object)(value ? Boolean.TRUE : Boolean.FALSE) );
        return this;
    }

    @Override
    public abstract JSONArray put( int index, Collection value ) throws JSONException ;

    @Override
    public JSONArray put( int index, double value ) throws JSONException {
        this.put( index, (Double)value );
        return this;
    }

    @Override
    public JSONArray put( int index, int value ) throws JSONException {
        this.put( index, (Integer)value );
        return this;
    }

    @Override
    public JSONArray put( int index, long value ) throws JSONException {
        this.put( index, (Long)value );
        return this;
    }

    @Override
    public JSONArray put( int index, JSONArray value ) throws JSONException {
        this.innerListSet( index, value );
        return this;
    }

    @Override
    public JSONArray put( int index, JSONObject value ) throws JSONException {
        this.innerListSet( index, value );
        return this;
    }

    @Override
    public abstract JSONArray put( int index, Map value ) throws JSONException ;

    @Override
    public JSONArray put( int index, Object value ) throws JSONException {
        JSONUtils.prospectNumberQualify(value);
        if ( index < 0 ) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        else {
            if ( index < this.length() ) {
                this.innerListSet( index, value );
            }
            else {
                while( index != this.length() ) {
                    this.put( JSON.NULL );
                }

                this.put( value );
            }

            return this;
        }
    }

    @Override
    public JSONObject toJSONObject( JSONArray names ) throws JSONException {
        if ( names != null && names.length() != 0 && this.length() != 0 ) {
            JSONObject jo = new JSONMaptron();

            for( int i = 0; i < names.length(); ++i ) {
                jo.put(names.getString(i), this.opt(i));
            }

            return jo;
        }

        return null;
    }

    @Override
    public JSONObject toJSONObject()  {
        JSONObject jo = new JSONMaptron();

        for( int i = 0; i < this.size(); ++i ) {
            jo.put( String.valueOf(i), this.opt( i ) );
        }

        return jo;
    }

    @Override
    public JSONArray toJSONArray()  {
        return this;
    }

    @Override
    public abstract Set<? > entrySet() ;

    @Override
    public Collection<Object > values() {
        return this;
    }

    @Override
    public Map<?, Object > toMap() {
        return this.toJSONObject();
    }

    @Override
    public List<Object > toList() {
        return this;
    }




    @Override
    public boolean hasOwnProperty( Object elm ) {
        return this.containsKey( elm );
    }

    @Override
    public boolean hasOwnProperty( int elm ) {
        return this.containsKey( elm );
    }

    @Override
    public boolean containsKey( Object elm ) {
        try {
            if( elm instanceof Number ) {
                return this.hasOwnProperty( ( (Number)elm ).intValue() );
            }
            return this.hasOwnProperty( (int)Integer.valueOf(elm.toString()) );
        }
        catch ( NumberFormatException e ){
            return false;
        }
    }

    @Override
    public boolean containsKey( int elm ) {
        int nLength = this.length();
        if( elm < 0 || nLength == 0 ){
            return false;
        }
        return nLength > elm;
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        try {
            return this.toJSONString(0);
        }
        catch (Exception e) {
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
        StringWriter sw = new StringWriter();
        synchronized(sw.getBuffer()) {
            return this.write( sw, nIndentFactor, 0 ).toString();
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
    public boolean isPrototypeOf( TypeIndex that ) {
        return that.equals( this.prototype() );
    }



    @Override
    public JSONArray clone() {
        try {
            return  (JSONArray) super.clone();
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
