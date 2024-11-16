package com.pinecone.framework.system.prototype;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.json.JSONArray;

public class MapStructuresEvaluator implements ObjectiveEvaluator {
    @Override
    public Object beanGet( Object that, String key ) {
        try {
            return this.beanGetExp( that, key );
        }
        catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            return null;
        }
    }

    @Override
    public Object beanGetExp( Object that, String key ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( that == null || key == null ) {
            return null;
        }
        String getterName = JavaBeans.MethodMajorKeyGet + Character.toUpperCase( key.charAt( 0 ) ) + key.substring( 1 );
        Method getter = that.getClass().getMethod( getterName );
        getter.setAccessible( true );
        return getter.invoke( that );
    }

    @Override
    public void beanSet( Object that, String key, Object val ) {
        try {
            this.beanSetExp( that, key, val );
        }
        catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored ) {
            // Do nothing.
        }
    }

    @Override
    public void beanSetExp( Object that, String key, Object val ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( that == null || key == null ) {
            return;
        }
        String setterName = JavaBeans.MethodMajorKeySet + Character.toUpperCase( key.charAt( 0 ) ) + key.substring( 1 );
        Method setter;
        if( val == null ) {
            setter = that.getClass().getMethod( setterName );
        }
        else {
            setter = that.getClass().getMethod( setterName, val.getClass() );
        }

        setter.setAccessible( true );
        setter.invoke( that, val );
    }

    @Override
    public Object structGet( Object that, String key ) {
        try {
            return this.structGetExp( that, key );
        }
        catch ( NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e ) {
            return null;
        }
    }

    public Object structGetExp( Object that, String key ) throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException {
        if ( that == null || key == null ) {
            return null;
        }
        Field field = that.getClass().getField( key );
        field.setAccessible( true );
        return field.get( that );
    }

    @Override
    public void structSet( Object that, String key, Object val ) {
        try {
            this.structSetExp( that, key, val );
        }
        catch ( NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException ignored ) {
            // Do nothing.
        }
    }

    public void structSetExp( Object that, String key, Object val ) throws NoSuchFieldException, SecurityException, IllegalAccessException, IllegalArgumentException {
        if ( that == null || key == null ) {
            return;
        }
        Field field = that.getClass().getField( key );
        field.setAccessible( true );
        field.set( that, val );
    }

    @Override
    public void classSet( Object that, String key, Object val ) {
        try {
            this.beanSetExp( that, key, val );
        }
        catch ( NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            this.structSet( that, key, val );
        }
    }

    @Override
    public Object classGet( Object that, String key ) {
        Object value = this.beanGet( that, key );
        if( value == null ) {
            return this.structGet( that, key );
        }
        return value;
    }

    @Override
    public Object get( Object that, String key ) {
        if ( that == null ) {
            return null;
        }

        if ( that instanceof Map ) {
            return ((Map<?, ?>) that).get(key);
        }
        else if ( that instanceof List ) {
            try {
                int index = Integer.parseInt( key );
                return ((List<?>) that).get(index);
            }
            catch ( NumberFormatException | IndexOutOfBoundsException e ) {
                return null;
            }
        }
        else if ( that.getClass().isArray() ) {
            try {
                int index = Integer.parseInt( key );
                return ((Object[]) that)[ index ];
            }
            catch ( NumberFormatException | ArrayIndexOutOfBoundsException e ) {
                return null;
            }
        }
        else if( that.getClass().isPrimitive() ) {
            return null;
        }
        else if( that.getClass().isEnum() ) {
            return null;
        }
        else if( that instanceof Number ) {
            return null;
        }
        else if( that instanceof String ) {
            return null;
        }
        else {
            return this.classGet( that, key );
        }
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void set( Object that, String key, Object val ) {
        if ( that == null ) {
            return;
        }

        if ( that instanceof Map ) {
            ((Map<String, Object>) that).put( key, val );
        }
        else if ( that instanceof List ) {
            try {
                int index = Integer.parseInt( key );
                ((List<Object>) that).set( index, val );
            }
            catch ( NumberFormatException | IndexOutOfBoundsException e ) {
                // Do nothing.
            }
        }
        else if ( that.getClass().isArray() ) {
            try {
                int index = Integer.parseInt( key );
                ((Object[]) that)[ index ] = val;
            }
            catch ( NumberFormatException | ArrayIndexOutOfBoundsException e ) {
                // Do nothing.
            }
        }

        if( that.getClass().isPrimitive() ) {
            return;
        }
        else if( that.getClass().isEnum() ) {
            return;
        }
        else if( that instanceof Number ) {
            return;
        }
        else if( that instanceof String ) {
            return;
        }

        this.classSet( that, key, val );
    }




    @Override
    public Class<?> beanGetType( Object that, String key ) {
        try {
            return this.beanGetTypeExp( that, key );
        }
        catch ( NoSuchMethodException | SecurityException | IllegalArgumentException e ) {
            return null;
        }
    }

    public Class<?> beanGetTypeExp( Object that, String key ) throws NoSuchMethodException, SecurityException, IllegalArgumentException {
        if ( that == null ) {
            return null;
        }
        if( key == null ) {
            return null;
        }

        String getterName = JavaBeans.MethodMajorKeyGet + Character.toUpperCase( key.charAt(0) ) + key.substring( 1 );
        return that.getClass().getMethod( getterName ).getReturnType();
    }

    @Override
    public Class<?> structGetType( Object that, String key ) {
        try {
            return this.structGetTypeWithException( that, key );
        }
        catch ( NoSuchFieldException | SecurityException e ) {
            return null;
        }
    }

    public Class<?> structGetTypeWithException( Object that, String key ) throws NoSuchFieldException, SecurityException {
        if ( that == null || key == null ) {
            return null;
        }
        Field field = that.getClass().getField( key );
        return field.getType();
    }

    @Override
    public Class<?> classGetType( Object that, String key ) {
        Class<?> type = this.beanGetType( that, key );
        if ( type == null ) {
            type = this.structGetType( that, key );
        }
        return type;
    }

    @Override
    public Class<?> getType( Object that, String key ) {
        if ( that == null ) {
            return null;
        }
        if ( that instanceof Map ) {
            Object value = ((Map<?, ?>) that).get( key );
            return value != null ? value.getClass() : Object.class;
        }
        else if ( that instanceof List ) {
            try {
                int index = Integer.parseInt( key );
                Object value ;
                if( that instanceof JSONArray ) {
                    value = ((JSONArray) that).opt( index );
                }
                else {
                    value = ((List<?>) that).get( index );
                }

                return value != null ? value.getClass() : Object.class;
            }
            catch ( NumberFormatException | IndexOutOfBoundsException e ) {
                return null;
            }
        }
        else if ( that.getClass().isArray() ) {
            return that.getClass().getComponentType();
        }
        else {
            return this.classGetType( that, key );
        }
    }
}
