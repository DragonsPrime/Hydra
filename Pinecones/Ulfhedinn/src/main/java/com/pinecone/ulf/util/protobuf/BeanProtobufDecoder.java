package com.pinecone.ulf.util.protobuf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.system.prototype.Pinenut;

public interface BeanProtobufDecoder extends Pinenut {

    Map decodeMap( Class<?> clazz, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options );

    default Map decodeMap( Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options ){
        return this.decodeMap( LinkedHashMap.class, descriptor, dynamicMessage, exceptedKeys, options );
    }

    <T > T decodeBean( Class<T> clazz, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options );

    <T > T decode( Class<T> clazz, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options );

}
