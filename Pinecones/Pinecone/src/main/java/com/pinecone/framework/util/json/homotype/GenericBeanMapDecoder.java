package com.pinecone.framework.util.json.homotype;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.StringUtils;

public class GenericBeanMapDecoder implements BeanMapDecoder {
    @SuppressWarnings( "unchecked" )
    protected Object decode0( Object bean, Map jo, Set<String > exceptedKeys, boolean bRecursive ) {
        return this.decode( bean, (Map<String, Object >)jo, exceptedKeys, bRecursive );
    }

    @Override
    public Object decode( Object bean, Map<String, Object > jo, Set<String > exceptedKeys, boolean bRecursive ) {
        if( jo == null ) {
            return bean;
        }

        Class klass = bean.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();

        for( int i = 0; i < methods.length; ++i ) {
            try {
                Method method = methods[i];
                if ( Modifier.isPublic( method.getModifiers() ) ) {
                    String legKey = JavaBeans.getSetterMethodKeyName( method );
                    String key = legKey;
                    if( !StringUtils.isEmpty( key ) ) {
                        if ( Character.isUpperCase( key.charAt(0) ) && method.getParameterTypes().length == 1 ) {
                            key = JavaBeans.methodKeyNameLowerCaseNormalize( key );

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
                                    String szGetterMethod = JavaBeans.MethodMajorKeyGet + legKey;
                                    Method      curGetter = bean.getClass().getMethod( szGetterMethod );
                                    if( curGetter != null ) {
                                        recursiveBean = curGetter.invoke( bean );
                                        if( !BeanMapDecoder.trialHomogeneity( recursiveBean ) ) {
                                            recursiveBean = null; // Not a bean.
                                        }
                                    }
                                }

                                if( recursiveBean == null ) {
                                    method.invoke( bean, desiredVal );
                                }
                                else {
                                    if( desiredVal instanceof Map ) {
                                        this.decode0( recursiveBean, (Map)desiredVal, exceptedKeys, bRecursive );
                                    }
                                }
                            }
                            catch ( IllegalAccessException | InvocationTargetException ignore ) {
                                ignore.printStackTrace();
                                // Do nothing.
                            }
                        }
                    }
                }
            }
            catch ( Exception ignore ) {
                ignore.printStackTrace();
                // Do nothing.
            }
        }

        return bean;
    }

    @Override
    public Object decode( Object bean, Map<String, Object > jo, boolean bRecursive ) {
        return this.decode( bean, jo, (Set<String >) null, bRecursive );
    }

    @Override
    public Object decode( Object bean, Map<String, Object> jo, Set<String> exceptedKeys ) {
        return this.decode( bean, jo, exceptedKeys, false );
    }

    @Override
    public Object decode( Object bean, Map<String, Object > jo ) {
        return this.decode( bean, jo, false );
    }
}
