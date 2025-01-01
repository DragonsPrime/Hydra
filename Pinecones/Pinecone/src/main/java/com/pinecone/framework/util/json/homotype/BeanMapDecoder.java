package com.pinecone.framework.util.json.homotype;

import java.util.Map;
import java.util.Set;

import com.pinecone.framework.system.prototype.Pinenut;


public interface BeanMapDecoder extends Pinenut {
    BeanMapDecoder BasicDecoder = new GenericBeanMapDecoder();

    static boolean    trialHomogeneity( Object that ) {
        return  JSONInjector.trialHomogeneity( that ) || that instanceof Map;
    }

    Object decode( Object bean, Map<String, Object > jo, Set<String > exceptedKeys, boolean bRecursive );

    Object decode( Object bean, Map<String, Object > jo, boolean bRecursive ) ;

    Object decode( Object bean, Map<String, Object > jo, Set<String > exceptedKeys );

    Object decode( Object bean, Map<String, Object > jo ) ;
}
