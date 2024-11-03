package com.pinecone.framework.util.json.hometype;

import com.pinecone.framework.system.functions.Executable;
import com.pinecone.framework.system.homotype.HomoInjector;
import com.pinecone.framework.system.homotype.StereotypicInjector;
import com.pinecone.framework.system.prototype.Prototype;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONArray;


import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class ObjectInjector implements HomoInjector, StereotypicInjector {
    protected Class<?> mType;

    protected String getFieldName( String szKey ){
        return szKey;
    }

    public ObjectInjector( Class type ) {
        this.mType = type;
    }

    @SuppressWarnings( "unchecked" )
    protected Object inject           ( Object that, Class<?> type ) {
        if ( ObjectInjector.trialHomogeneity( that ) && !type.isEnum() ){
            return that;
        }
        else if( type == Object.class ){
            return that;
        }
        else if( that instanceof Executable ){
            return this.inject( (Executable) that );
        }
        else if ( that instanceof Collection ){
            return this.inject( (Collection) that, type );
        }
        else if ( that instanceof Map ){
            return this.inject( (Map) that, type );
        }
        else if ( type.isEnum() ){
            try{
                return Enum.valueOf( (Class<Enum>) type, that.toString() );
            }
            catch ( RuntimeException e ) {
                return that;
            }
        }
        return that;
    }

    public static Collection newDefaultCollection( Class<?> type ) {
        if( type == null || List.class.isAssignableFrom( type ) ){
            return new ArrayList();
        }
        else if( Set.class.isAssignableFrom( type ) ){
            return new HashSet();
        }
        else if( Queue.class.isAssignableFrom( type ) ){
            return new LinkedList();
        }
        else {
            return new ArrayList();
        }
    }


    public    Object injectArray      ( Collection that, Class<?> type, Object instance ) {
        Class innerType = type.getComponentType();
        if( innerType.isPrimitive() ){
            int i = 0;
            for ( Object o : that ) {
                Array.set( instance, i, this.inject( o, innerType ) );
                ++i;
            }
            return instance;
        }
        else {
            Object[] objects = (Object[]) instance;
            int i = 0;
            for ( Object o : that ) {
                objects[ i ] = this.inject( o, innerType );
                ++i;
            }
            return objects;
        }
    }

    public    Object injectCollection ( Collection that, Class<?> type, Collection instance ) {
        for( Object row : that ){
            instance.add( this.inject( row, row.getClass() ) );
        }

        return instance;
    }

    protected Object inject           ( Collection that, Class<?> type ) {
        if( type != null && type.isAssignableFrom( that.getClass() ) ){
            return that;
        }
        else if( type == null || Collection.class.isAssignableFrom( type )  ){
            Collection instance;
            if( type == null || type.isInterface() || Prototype.isAbstract( type ) ){ // Motherfucker condition...
                instance = ObjectInjector.newDefaultCollection( type );
            }
            else {
                try{
                    instance = (Collection)type.getDeclaredConstructor().newInstance();
                }
                catch ( InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
                    instance = ObjectInjector.newDefaultCollection( type );
                }
            }

            return this.injectCollection( that, type, instance );
        }
        else if( type.isArray() ){
            Class innerType = type.getComponentType();
            Object instance = Array.newInstance( innerType, that.size() );
            return this.injectArray( that, type, instance );
        }

        return null; // WHat fuck could be ??? asking jesus...
    }

    public    Object inject           ( Collection that, Class<?> type, Object instance ) {
        if( type == JSONArray.class || type == Object.class ){
            return that;
        }
        else if( type == null || Collection.class.isAssignableFrom( type )  ){
            return this.injectCollection( that, type, (Collection) instance );
        }
        else if( type.isArray() ){
            return this.injectArray( that, type, instance );
        }

        return null; // WHat fuck could be ??? asking jesus...
    }

    protected Object inject           ( Map that, Class<?> type ) {
        if( type != null && type.isAssignableFrom( that.getClass() ) ){
            return that;
        }
        else if( type == null || Map.class.isAssignableFrom( type )  ){
            Map map;
            if( type == null || type.isInterface() || Prototype.isAbstract( type ) ){ // Motherfucker condition...
                map = new LinkedHashMap();
            }
            else {
                try{
                    map = (Map)type.getDeclaredConstructor().newInstance();
                }
                catch ( InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
                    map = new LinkedHashMap();
                }
            }

            for( Object row : that.entrySet() ){
                Map.Entry kv = (Map.Entry) row;
                map.put( kv.getKey(), this.inject( kv.getValue(), type ) );
            }
            return map;
        }
        else  {
            Object obj;
            try{
                Constructor constructor = type.getConstructor();
                ReflectionUtils.makeAccessible( constructor );
                obj = constructor.newInstance();
            }
            catch ( NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e ) {
                return null;
            }

            return this.inject( that, obj );
        }
    }

    public    Object inject           ( Map that, Class<?> type, Object instance ) {
        for( Object row : that.entrySet() ){
            Map.Entry kv = (Map.Entry) row;
            Field field;
            try{
                field = type.getDeclaredField( this.getFieldName( kv.getKey().toString() ) );
            }
            catch ( NoSuchFieldException e ){
                try {
                    field = type.getDeclaredField( kv.getKey().toString() );
                }
                catch ( NoSuchFieldException e1 ){
                    field = null;
                }
            }
            if( field != null ){
                ReflectionUtils.makeAccessible( field );
                try {
                    try {
                        Object j = this.inject( kv.getValue() , field.getType() );
                        field.set( instance, j );
                    }
                    catch ( IllegalArgumentException e ){
                        field = null;
                    }
                }
                catch ( IllegalAccessException e ){
                    throw new IllegalStateException(e); // This should never be happened.
                }
            }
        }

        /*    Field[] fields = type.getClass().getDeclaredFields();
            for ( Field field : fields ) {
                ReflectionUtils.makeAccessible( field );
                try {
                    Object val = that.opt( this.getFieldName( field.getName() ) );
                    if( val == null ){
                        val = that.opt( field.getName() );
                    }
                    try {
                        Object j = this.inject( val , field.getType() );
                        field.set( type, j );
                    }
                    catch ( IllegalArgumentException e ){
                        e.printStackTrace();
                    }
                }
                catch ( IllegalAccessException e ){
                    throw new IllegalStateException(e); // This should never be happened.
                }
            }*/

        return instance;
    }

    public    Object inject           ( Map that, Object instance ) {
        if( this.mType != null ) {
            return this.typeInject( that, instance );
        }
        return this.inject( that, instance.getClass(), instance );
    }

    public    Object typeInject       ( Map that, Object instance ) {
        return this.inject( that, this.mType, instance );
    }



    public    Object inject           ( Collection that ){
        return this.inject( that, this.mType );
    }

    public    Object inject           ( Map that ){
        return this.inject( that, this.mType );
    }

    public    Object inject           ( Executable data ) {
        Method fn;
        try {
            fn = data.getClass().getMethod( "reveal" );
            try {
                return fn.invoke( data );
            }
            catch ( Exception e ){
                throw new IllegalArgumentException( "Executable `reveal` function should never be modified.", e ); // What fuck was that, did you modified it ?
            }
        }
        catch ( NoSuchMethodException e ){
            return data;
        }
    }

    public    Number inject           ( Number data ){
        return data;
    }

    public    Boolean inject          ( Boolean data ){
        return data;
    }

    public    String inject           ( String data ){
        return data;
    }

    public    Runnable inject         ( Runnable data ){
        return data;
    }

    public    Callable inject         ( Callable data ){
        return data;
    }

    public    Method inject           ( Method data ){
        return data;
    }

    @Override
    public Object inject              ( Object that ){
        return this.inject( that, this.mType );
    }

    @Override
    public Object inject              ( Object that, Object instance ) throws Exception {
        if( this.mType != null ) {
            return this.inject( that, this.mType, instance );
        }
        return this.inject( that, instance.getClass(), instance );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Object inject              ( Object that, Class<?> type, Object instance ) throws Exception {
        if ( ObjectInjector.trialHomogeneity( that ) && !type.isEnum() ){
            return that;
        }
        else if( type == Object.class ){
            return that;
        }
        else if( that instanceof Executable){
            return this.inject( (Executable) that );
        }
        else if ( that instanceof Collection ){
            return this.inject( (Collection) that, type, instance );
        }
        else if ( that instanceof Map ){
            return this.inject( (Map) that, type, instance );
        }
        else if ( type.isEnum() ){
            try{
                return Enum.valueOf( (Class<Enum>) type, that.toString() );
            }
            catch ( RuntimeException e ) {
                return that;
            }
        }
        return that;
    }


    @Override
    public boolean isHomogeneity     ( Object that ){
        return ObjectInjector.trialHomogeneity( that );
    }

    @Override
    public Class<?> getStereotype() {
        return this.mType;
    }

    @Override
    public void setStereotype( Class<?> type ) {
        this.mType = type;
    }

    public static boolean trialHomogeneity( Object that ) {
        return  that instanceof Number     || that instanceof Boolean   || that instanceof String || that == JSON.NULL ||
                that instanceof Callable   || that instanceof Runnable  || that instanceof Method;
    }

}