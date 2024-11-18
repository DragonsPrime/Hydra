package com.pinecone.framework.system.prototype;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pinecone.framework.unit.Units;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.homotype.StructJSONEncoder;

public class ObjectiveClass implements Objectom {
    protected Object    mObj;
    protected Entry[]   mFields;
    protected boolean   mbUsingOrderCache;

    public ObjectiveClass( Object that, boolean bUsingOrderCache ) {
        this.mObj = that;
        this.mbUsingOrderCache = bUsingOrderCache;
        if ( bUsingOrderCache ) {
            this.cacheFields();
        }
    }

    public ObjectiveClass( Object that ) {
        this( that, true );
    }

    @Override
    public int size() {
        if( this.mFields != null ) {
            return this.mFields.length;
        }
        return this.mObj.getClass().getFields().length;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    private void cacheFields() {
        Field[] classFields = this.mObj.getClass().getFields();
        this.mFields = new Entry[ classFields.length ];
        for ( int i = 0; i < classFields.length; ++i ) {
            Field field        = classFields[ i ];
            String fieldName   = field.getName();
            this.mFields[ i ]  = new Entry( fieldName, field );
        }
        Arrays.sort( this.mFields );
    }

    public Object get( Object key ) {
        String szKey = key.toString();
        try {
            if ( this.mbUsingOrderCache ) {
                int index = this.binarySearch( szKey );
                if ( index >= 0 ) {
                    ReflectionUtils.makeAccessible( this.mFields[index].field );
                    return this.mFields[ index ].field.get( this.mObj );
                }
            }
            else {
                Field field = this.mObj.getClass().getField( szKey );
                ReflectionUtils.makeAccessible( field );
                return field.get( this.mObj );
            }
        }
        catch ( NoSuchFieldException | IllegalAccessException e ) {
            return null;
        }
        return null;
    }

    protected int binarySearch( String key ) {
        int low = 0;
        int high = this.mFields.length - 1;
        while ( low <= high ) {
            int mid = (low + high) >>> 1;
            int cmp = this.mFields[ mid ].name.compareTo( key );
            if ( cmp < 0 ) {
                low = mid + 1;
            }
            else if ( cmp > 0 ) {
                high = mid - 1;
            }
            else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public void set( Object key, Object val ) {
        String szKey = key.toString();
        try {
            if ( this.mbUsingOrderCache ) {
                int index = this.binarySearch( szKey );
                if ( index >= 0 ) {
                    ReflectionUtils.makeAccessible( this.mFields[ index ].field );
                    this.mFields[index].field.set( this.mObj, val );
                    return;
                }
            }
            else {
                Field field = this.mObj.getClass().getField( szKey );
                ReflectionUtils.makeAccessible( field );
                field.set( this.mObj, val );
                return;
            }
        }
        catch ( NoSuchFieldException | IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
        throw new IllegalArgumentException( "Field not found: " + key );
    }

    @Override
    public boolean hasOwnProperty( Object k ) {
        if ( this.mObj instanceof PineUnit ) {
            return ( (PineUnit) this.mObj ).hasOwnProperty(k);
        }
        return this.containsKey(k);
    }

    @Override
    public boolean containsKey( Object k ) {
        String szKey = k.toString();
        try {
            if ( this.mbUsingOrderCache ) {
                return this.binarySearch( szKey ) != -1;
            }
            else {
                Field field = this.mObj.getClass().getField( szKey );
                return field != null;
            }
        }
        catch (NoSuchFieldException e) {
            return false;
        }
    }

    @Override
    public String toJSONString() {
        return StructJSONEncoder.BasicEncoder.encode( this.mObj );
    }

    @Override
    public Map<String, Object > toMap( Class<? > mapType ) {
        Map<String, Object > map = Units.newInstance( mapType );
        Field[] classFields = this.mObj.getClass().getFields();
        this.mFields = new Entry[ classFields.length ];
        for ( int i = 0; i < classFields.length; ++i ) {
            Field field        = classFields[ i ];
            String fieldName   = field.getName();
            try {
                map.put( fieldName, field.get( this.mObj ) );
            }
            catch ( IllegalAccessException e ) {
                try {
                    field.setAccessible( true );
                    map.put( fieldName, field.get( this.mObj ) );
                    field.setAccessible( false );
                }
                catch ( IllegalAccessException ignore ) {
                    // Do nothing.
                }
            }
        }

        return map;
    }

    @Override
    public Map<String, Object > toMap() {
        return this.toMap( LinkedHashMap.class );
    }

    @Override
    public TypeIndex prototype() {
        return Prototype.typeid( this.mObj );
    }

    @Override
    public String prototypeName() {
        return Prototype.prototypeName( this.mObj );
    }

    private static class Entry implements Comparable<Entry> {
        String name;
        Field field;

        Entry( String name, Field field ) {
            this.name  = name;
            this.field = field;
        }

        @Override
        public int compareTo( Entry o ) {
            return this.name.compareTo( o.name );
        }
    }

    @Override
    public String[] keys() {
        int size = this.size(); // Saving some logic operations.

        String[] list = new String[ size ];
        if( this.mFields != null && this.mFields.length > 0 ) {
            for ( int i = 0; i < size; ++i ) {
                list[ i ] = this.mFields[i].name;
            }
        }
        else {
            Field[] classFields = this.mObj.getClass().getFields();
            for ( int i = 0; i < size; ++i ) {
                list[ i ] = classFields[i].getName();
            }
        }
        return list;
    }
}