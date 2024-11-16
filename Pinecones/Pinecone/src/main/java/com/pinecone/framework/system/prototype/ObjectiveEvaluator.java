package com.pinecone.framework.system.prototype;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

public interface ObjectiveEvaluator extends Pinenut {
    ObjectiveEvaluator MapStructures = new MapStructuresEvaluator();

    Object beanGet( Object that, String key );

    Object beanGetExp( Object that, String key ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    default Object beanGet( Object that, Object key ) {
        return this.beanGet( that, key.toString() );
    }

    Object structGet( Object that, String key );

    default Object structGet( Object that, Object key ) {
        return this.structGet( that, key.toString() );
    }

    Object get( Object that, String key );

    default Object get( Object that, Object key ) {
        return this.get( that, key.toString() );
    }

    Object classGet( Object that, String key );

    default Object classGet( Object that, Object key ) {
        return this.classGet( that, key.toString() );
    }




    void beanSet( Object that, String key, Object val );

    void beanSetExp( Object that, String key, Object val ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    default void beanSet( Object that, Object key, Object val ) {
        this.beanSet( that, key.toString(), val );
    }

    void structSet( Object that, String key, Object val );

    default void structSet( Object that, Object key, Object val ) {
        this.structSet( that, key.toString(), val );
    }

    void set( Object that, String key, Object val );

    void classSet( Object that, String key, Object val );

    default void classSet( Object that, Object key, Object val ) {
        this.classSet( that, key.toString(), val );
    }

    default void set( Object that, Object key, Object val ) {
        this.set( that, key.toString(), val );
    }



    Class<?> beanGetType( Object that, String key );

    Class<?> beanGetTypeExp( Object that, String key ) throws NoSuchMethodException, SecurityException, IllegalArgumentException;

    default Class<?> beanGetType( Object that, Object key ) {
        return this.beanGetType( that, key.toString() );
    }

    Class<?> structGetType( Object that, String key );

    default Class<?> structGetType( Object that, Object key ) {
        return this.structGetType( that, key.toString() );
    }

    Class<?> getType( Object that, String key );

    default Class<?> getType( Object that, Object key ) {
        return this.getType( that, key.toString() );
    }

    Class<?> classGetType( Object that, String key );

    default Class<?> classGetType( Object that, Object key ) {
        return this.classGetType( that, key.toString() );
    }

    default Type getFieldGenericType( Object obj, String fieldName ) {
        Type fieldGenericType = null;
        try{
            if( obj != null ) {
                Field field      = obj.getClass().getDeclaredField( fieldName );
                fieldGenericType = field.getGenericType();
            }
        }
        catch ( NoSuchFieldException | SecurityException e ) {
            fieldGenericType = null;
        }

        return fieldGenericType;
    }
}
