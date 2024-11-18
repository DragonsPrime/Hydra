package com.pinecone.framework.util.json.homotype;

import com.pinecone.framework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class AnnotatedObjectInjector extends ObjectInjector {
    public AnnotatedObjectInjector( Class type ) {
        super( type );
    }

    protected String getAnnotatedKey( Field field ) {
        return AnnotatedJSONInjector.getAnnotatedKey( field );
    }

    @Override
    public    Object inject      ( Map that, Class<?> type, Object instance ) {
        Field[] fields = type.getDeclaredFields();
        for ( Field field : fields ) {
            ReflectionUtils.makeAccessible( field );
            try {
                String szKey = this.getAnnotatedKey( field );
                if( szKey == null ) {
                    continue;
                }
                else if( szKey.isEmpty() ) {
                    szKey = field.getName();
                }

                Object val = that.get( this.getFieldName( szKey ) );
                if( val == null ){
                    val = that.get( szKey );
                }
                if( val == null && szKey.contains( "." ) ){
                    val = this.getValueFromMapRecursively( that, szKey );
                }

                try {
                    Object j = this.inject( val , field.getType() );
                    field.set( instance, j );
                }
                catch ( IllegalArgumentException e ){
                    //e.printStackTrace();
                    field = null;
                }
            }
            catch ( IllegalAccessException e ){
                throw new IllegalStateException(e); // This should never be happened.
            }
        }

        return instance;
    }

    protected Object getValueFromMapRecursively( Map map, String key ) {
        String[] keys = key.split("\\.|\\/");
        Object value = map;
        for ( String k : keys ) {
            if ( value instanceof Map ) {
                value = ((Map) value).get(k);
            }
            else if ( value instanceof List ) {
                try{
                    value = ((List) value).get( Integer.parseInt( k ) );
                }
                catch ( NumberFormatException e ) {
                    return null;
                }
            }
            else {
                return null;
            }
        }
        return value;
    }
}
