package com.pinecone.framework.util.json.homotype;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.JSONUtils;

public abstract class ArchBeanColonist implements BeanColonist {
    public ArchBeanColonist() {

    }

    @Override
    public void populate( Object bean, JSONObject target, boolean bRecursive ) {
        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        Map<String, Object > targetMap = target.getMap();

        for( int i = 0; i < methods.length; ++i ) {
            try {
                Method method = methods[i];
                if ( Modifier.isPublic( method.getModifiers() ) ) {
                    String key = JavaBeans.getGetterMethodKeyName( method );
                    if( key == null ) {
                        continue;
                    }

                    if ( key.length() > 0 && Character.isUpperCase( key.charAt(0) ) && method.getParameterTypes().length == 0 ) {
                        key = JavaBeans.methodKeyNameLowerCaseNormalize( key );

                        method.setAccessible( true );
                        Object result = method.invoke( bean, (Object[])null );

                        this.putValue( targetMap, key, result, bRecursive );
                    }
                }
            }
            catch ( InvocationTargetException | IllegalAccessException e ) {
                e.printStackTrace();
                // Do nothing.
            }
        }
    }

    protected void putValue( Map<String, Object > targetMap, String key, Object result, boolean bRecursive ) {
        if ( result != null ) {
            Object v = JSONUtils.wrapValue( result, bRecursive );
            if( v == null ) {
                v = result;
            }
            targetMap.put( key, v );
        }
    }
}
