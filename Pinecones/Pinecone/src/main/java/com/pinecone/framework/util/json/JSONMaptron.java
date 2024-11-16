package com.pinecone.framework.util.json;

import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.hometype.BeanColonist;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

public class JSONMaptron extends ArchJSONObject implements JSONObject, Serializable {
    private Map<String, Object > mMap;

    public JSONMaptron() {
        this( true );
    }

    public JSONMaptron( boolean bLinked ){
        this.mMap = bLinked ? new LinkedHashMap<>() : new HashMap<>();
    }

    public JSONMaptron( int nInitialCapacity, boolean bLinked ){
        if ( bLinked ) {
            this.mMap = new LinkedHashMap<>( nInitialCapacity );
        }
        else {
            this.mMap = new HashMap<>( nInitialCapacity );
        }
    }

    public JSONMaptron( ArchCursorParser x ) throws JSONException {
        this();
        this.jsonDecode0( x );
    }

    public JSONMaptron( Map<String, Object> map, String[] names ) {
        this();

        for( int i = 0; i < names.length; ++i ) {
            try {
                this.putOnce( names[i], map.get( names[i] ) );
            }
            catch ( Exception e ) {
                this.putOnce( names[i], JSON.NULL );
            }
        }

    }

    public JSONMaptron( Map<String, Object> map ) {
        this( map,false );
    }

    public JSONMaptron( Map<String, Object> map, boolean bAssimilateMode ) {
        if( bAssimilateMode ){
            this.mMap = map;
        }
        else {
            this.mMap = new LinkedHashMap<>();
            if (map != null) {
                for ( Object o : map.entrySet() ) {
                    Entry e = (Entry) o;
                    Object value = e.getValue();
                    if (value != null) {
                        this.mMap.put( (String) e.getKey(), JSONUtils.wrapValue(value) );
                    }
                }
            }
        }
    }

    public JSONMaptron( Object bean ) {
        this();
        this.populateMap( bean );
    }

    public JSONMaptron( Object object, String[] names ) {
        this();
        Class c = object.getClass();

        for( int i = 0; i < names.length; ++i ) {
            String name = names[i];

            try {
                this.putOpt( name, c.getField(name).get(object) );
            }
            catch ( Exception ignore ) {
                //Do nothing.
            }
        }

    }

    public JSONMaptron( String source ) throws JSONException {
        this(new JSONCursorParser(source));
    }

    public JSONMaptron( String baseName, Locale locale ) throws JSONException {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
        Enumeration keys = bundle.getKeys();

        while( true ) {
            Object key;
            do {
                if ( !keys.hasMoreElements() ) {
                    return;
                }

                key = keys.nextElement();
            }
            while(!(key instanceof String));

            String[] path = ((String)key).split("\\.");
            int last = path.length - 1;
            JSONObject target = this;

            for( int i = 0; i < last; ++i ) {
                String segment = path[i];
                JSONObject nextTarget = target.optJSONObject(segment);
                if ( nextTarget == null ) {
                    nextTarget = new JSONMaptron();
                    target.put(segment, (Object)nextTarget);
                }

                target = nextTarget;
            }

            target.put(path[last], (Object)bundle.getString((String)key));
        }
    }

    @Override
    protected void jsonDecode0( ArchCursorParser x ) throws JSONException {
        JSONObjectDecoder.INNER_JSON_OBJECT_DECODER.decode( this, null, null, x, null );
    }

    @Override
    public JSONMaptron jsonDecode( ArchCursorParser x ) throws JSONException {
        this.clear();
        this.jsonDecode0( x );
        return this;
    }

    @Override
    public JSONMaptron jsonDecode( String source ) throws JSONException {
        return this.jsonDecode( new JSONCursorParser(source) );
    }


    @Override
    public JSONMaptron assimilate( Map<String, Object> that ){
        this.mMap = that;
        return this;
    }



    @Override
    public Map<String, Object > getMap(){
        return this.mMap;
    }

    /** Basic Map **/
    @Override
    public int size() {
        return this.mMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.mMap.isEmpty();
    }

    @Override
    protected boolean innerMapContainsKey( Object key ) {
        return this.mMap.containsKey( key );
    }

    @Override
    public boolean containsValue( Object value ) {
        return this.mMap.containsValue(value);
    }

    @Override
    public void putAll( Map<? extends String, ?> m ) {
        this.mMap.putAll(m);
    }

    @Override
    public void clear() {
        this.mMap.clear();
    }

    @Override
    public Object remove( Object key ) {
        return this.mMap.remove(key);
    }

    @Override
    public Set<String > keySet() {
        return this.mMap.keySet();
    }

    @Override
    public Collection<Object > values() {
        return this.mMap.values();
    }

    @Override
    public Set<Map.Entry<String, Object > > entrySet() {
        return this.mMap.entrySet();
    }


    @Override
    protected Object innerMapGet( Object key ) {
        return this.mMap.get( key );
    }




    protected void populateMap( Object bean ) {
        BeanColonist.WrappedColonist.populate( bean, this );
    }

    @Override
    protected Object innerMapPut( String key, Object value ){
        return this.mMap.put( key, value );
    }

    @Override
    public JSONMaptron put( String key, Map value ) throws JSONException {
        this.put( key, (Object)( new JSONMaptron(value) ) );
        return this;
    }

    @Override
    protected Object innerMapRemove( String key ) {
        return this.mMap.remove( key );
    }

    @Override
    public Map.Entry<String, Object > front() {
        return this.mMap.entrySet().iterator().next();
    }

    @Override
    public Map.Entry<String, Object > back() {
        try{
            if( this.mMap instanceof LinkedHashMap ){
                Field tail = this.mMap.getClass().getDeclaredField("tail" );
                tail.setAccessible( true );
                Map.Entry<?, ?> kv = (Map.Entry<?, ?> )tail.get( this.mMap );
                return (Map.Entry<String, Object> ) kv;
            }
            else if( this.mMap instanceof LinkedTreeMap ){
                return ( (LinkedTreeMap<String, Object>)this.mMap ).getLast();
            }
            else if( this.mMap instanceof TreeMap ){
                return ( (TreeMap<String, Object>)this.mMap ).lastEntry();
            }
            else {
                throw new IllegalStateException();
            }
        }
        catch ( NoSuchFieldException | IllegalAccessException | IllegalStateException e ) {
            // It seem there is the only way, fuck.
            Iterator<Map.Entry<String, Object> > iterator = this.mMap.entrySet().iterator();
            Map.Entry<String, Object> tail    = null;
            while ( iterator.hasNext() ) {
                tail = iterator.next();
            }
            return tail;
        }
    }





    @Override
    public JSONMaptron clone() {
        JSONMaptron that = (JSONMaptron) super.clone();
        that.mMap = new LinkedHashMap<>();
        for ( Entry<String, Object> e : this.mMap.entrySet() ) {
            Object value = e.getValue();
            that.mMap.put( e.getKey(), JSONUtils.cloneElement( value ) );
        }
        return that;
    }

    @Override
    public Writer write( Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        return JSONEncoder.BASIC_JSON_ENCODER.write( this.mMap, writer, nIndentFactor, nIndentBlankNum );
    }
}
