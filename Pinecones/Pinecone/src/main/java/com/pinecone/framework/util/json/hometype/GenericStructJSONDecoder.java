package com.pinecone.framework.util.json.hometype;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.StringUtils;

public class GenericStructJSONDecoder implements StructJSONDecoder {
    @SuppressWarnings( "unchecked" )
    protected Object decode0( Object struct, Map jo, Set<String > exceptedKeys, boolean bRecursive ) {
        return this.decode( struct, (Map<String, Object >)jo, exceptedKeys, bRecursive );
    }

    @Override
    public Object decode( Object struct, Map<String, Object > jo, Set<String > exceptedKeys, boolean bRecursive ) {
        if( jo == null ) {
            return struct;
        }

        Class klass = struct.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Field[] fields = includeSuperClass ? klass.getFields() : klass.getDeclaredFields();

        for( int i = 0; i < fields.length; ++i ) {
            try {
                Field field = fields[i];
                field.setAccessible( true );

                String key = field.getName();
                if( !StringUtils.isEmpty( key ) ) {
                    Object desiredVal = jo.get( key );
                    if( desiredVal == null ) {
                        continue;
                    }
                    else if( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                        continue;
                    }

                    try {
                        Object recursiveBean = null;
                        if( bRecursive ) {
                            Field      curField = struct.getClass().getField( key );
                            if( curField != null ) {
                                recursiveBean = curField.get( struct );
                                if( !BeanJSONDecoder.trialHomogeneity( recursiveBean ) ) {
                                    recursiveBean = null; // Not a struct.
                                }
                            }
                        }

                        if( recursiveBean == null ) {
                            field.set( struct, desiredVal );
                        }
                        else {
                            if( desiredVal instanceof Map ) {
                                this.decode0( recursiveBean, (Map)desiredVal, exceptedKeys, bRecursive );
                            }
                        }
                    }
                    catch ( IllegalAccessException | IllegalArgumentException ignore ) {
                        ignore.printStackTrace();
                        // Do nothing.
                    }
                }
            }
            catch ( Exception ignore ) {
                ignore.printStackTrace();
                // Do nothing.
            }
        }

        return struct;
    }

    @Override
    public Object decode( Object struct, Map<String, Object > jo, boolean bRecursive ) {
        return this.decode( struct, jo, (Set<String >) null, bRecursive );
    }

    @Override
    public Object decode( Object struct, Map<String, Object> jo, Set<String> exceptedKeys ) {
        return this.decode( struct, jo, exceptedKeys, false );
    }

    @Override
    public Object decode( Object struct, Map<String, Object > jo ) {
        return this.decode( struct, jo, false );
    }
}