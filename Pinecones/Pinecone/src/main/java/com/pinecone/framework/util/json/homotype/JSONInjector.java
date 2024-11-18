package com.pinecone.framework.util.json.homotype;

import com.pinecone.framework.system.functions.Executable;
import com.pinecone.framework.system.functions.Executor;
import com.pinecone.framework.system.functions.Function;
import com.pinecone.framework.system.homotype.HomoInjector;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class JSONInjector implements HomoInjector {
    public JSONInjector(){

    }

    @Override
    public boolean isHomogeneity( Object that ) {
        return JSONInjector.trialHomogeneity( that );
    }

    protected String getFieldName( Field field ){
        return field.getName();
    }

    @Override
    public Object  inject( Object data ) throws IllegalArgumentException {
        return this.inject( data, true );
    }

    @Override
    public Object inject              ( Object that, Object instance ) throws Exception {
        return this.inject( that, that.getClass(), instance );
    }

    @Override
    public Object inject              ( Object that, Class<?> stereotype, Object instance ) throws Exception {
        return this.inject( true, true, instance );
    }

    public Object  inject             ( Object data, boolean bRecursive ) throws IllegalArgumentException  {
        return this.inject( data, bRecursive, null );
    }

    public Object  inject             ( Object data, boolean bRecursive, Object instance ) throws IllegalArgumentException  {
        if ( data != null ) {
            if ( JSONInjector.trialHomogeneity( data ) ) {
                return data;
            }
            else if ( data instanceof Map) {
                return new JSONMaptron((Map)data);
            }
            else if ( data instanceof List) {
                return new JSONArraytron((List) data);
            }
            else if ( data instanceof Collection ) {
                return new JSONArraytron((Collection)data);
            }
            else if ( data instanceof JSONString ) {
                return ( (JSONString) data ).toJSONString();
            }
            else {
                return this.javaObjectInject( data, bRecursive, instance );
            }
        }

        return JSON.NULL;
    }

    public Object  injectArray        ( Object data, boolean bRecursive, JSONArray instance ) throws IllegalArgumentException {
        if( data.getClass().getComponentType().isPrimitive() ){
            for ( int i = 0; i < Array.getLength(data); i++ ) {
                instance.put( this.inject( Array.get( data, i ), bRecursive ) );
            }
        }
        else {
            for ( Object row : (Object[]) data ) {
                instance.put( this.inject( row, bRecursive ) );
            }
        }
        return instance;
    }

    public Object  injectObject       ( Object data, boolean bRecursive, JSONObject instance ) throws IllegalArgumentException {
        Field[] fields = data.getClass().getDeclaredFields();
        for ( Field field : fields ) {
            ReflectionUtils.makeAccessible( field );
            String szKey = this.getFieldName( field );
            if( szKey == null ) {
                continue;
            }

            Object value;
            try{
                value = field.get( data );
            }
            catch ( IllegalAccessException e ){
                value = null;
            }
            instance.embed( this.getFieldName( field ), this.inject( value, bRecursive ) );
        }
        return instance;
    }

    public Object  javaObjectInject   ( Object data, boolean bRecursive, Object instance ) throws IllegalArgumentException {
        if( data != null ){
            if( data.getClass().isArray() ){
                JSONArray array = (JSONArray) instance;
                if( instance == null ) {
                    array = new JSONArraytron();
                }
                return this.injectArray( data, bRecursive, array );
            }
            else if( data.getClass().isEnum() ) {
                return data.toString();
            }
            else if( data instanceof Method ){
                throw new IllegalArgumentException( "Method cannot survive without its mother." );
            }
            else if( data instanceof Runnable ){
                return new Executor() {
                    Runnable proto = (Runnable) data;

                    public Runnable reveal(){
                        return this.proto;
                    }

                    @Override
                    public void execute() throws Exception {
                        this.proto.run();
                    }
                };
            }
            else if( data instanceof Callable ){
                return new Function() {
                    Callable proto = (Callable) data;

                    public Callable reveal(){
                        return this.proto;
                    }

                    @Override
                    public Object invoke( Object... obj ) throws Exception {
                        return this.proto.call();
                    }
                };
            }
            else {
                JSONObject object = (JSONObject) instance;
                if( instance == null ) {
                    object = new JSONMaptron();
                }
                return this.injectObject( data, bRecursive, object );
            }

        }
        return JSON.NULL;
    }




    public static Number     inject( Number data ){
        return data;
    }

    public static Boolean    inject( Boolean data ){
        return data;
    }

    public static String     inject( String data ){
        return data;
    }

    public static Executable inject( Executable data ){
        return data;
    }

    public static JSONObject inject( JSONObject data ){
        return data;
    }

    public static JSONArray  inject( JSONArray data ){
        return data;
    }

    public static boolean    trialHomogeneity( Object that ) {
        return  that instanceof Number     || that instanceof Boolean   || that instanceof String || that == JSON.NULL ||
                that instanceof JSONObject || that instanceof JSONArray ||
                that instanceof Executable;
    }
}
