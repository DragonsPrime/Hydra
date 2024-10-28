package com.pinecone.framework.util.json.hometype;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.StringUtils;


public interface BeanJSONDecoder extends Pinenut {
    BeanJSONDecoder BasicDecoder = new GenericBeanJSONDecoder();

    static boolean    trialHomogeneity( Object that ) {
        return  JSONInjector.trialHomogeneity( that ) || that instanceof Map;
    }

    Object decode( Object bean, Map<String, Object > jo, Set<String > exceptedKeys, boolean bRecursive );

    Object decode( Object bean, Map<String, Object > jo, boolean bRecursive ) ;

    Object decode( Object bean, Map<String, Object > jo, Set<String > exceptedKeys );

    Object decode( Object bean, Map<String, Object > jo ) ;
}
