package com.pinecone.framework.util.json.hometype;

import java.util.Map;
import java.util.Set;

import com.pinecone.framework.system.prototype.Pinenut;

public interface StructJSONDecoder extends Pinenut {
    StructJSONDecoder BasicDecoder = new GenericStructJSONDecoder();

    static boolean    trialHomogeneity( Object that ) {
        return  JSONInjector.trialHomogeneity( that ) || that instanceof Map;
    }

    Object decode( Object struct, Map<String, Object > jo, Set<String > exceptedKeys, boolean bRecursive );

    Object decode( Object struct, Map<String, Object > jo, boolean bRecursive ) ;

    Object decode( Object struct, Map<String, Object > jo, Set<String > exceptedKeys );

    Object decode( Object struct, Map<String, Object > jo ) ;
}

