package com.pinecone.ulf.util.protobuf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.system.prototype.Pinenut;

public interface BeanProtobufEncoder extends Pinenut {
    Descriptors.Descriptor transform( Object dynamicObject, Set<String > exceptedKeys, Options options );

    Descriptors.Descriptor transform( Map dynamicObject, Set<String > exceptedKeys, Options options );

    DescriptorProtos.FieldDescriptorProto.Builder transform( Collection dynamicObject, Class<? > elementType, String key, int fieldNumber, Options options );

    //Descriptors.Descriptor transform( Object[] dynamicObject, Set<String > exceptedKeys, Options options );

    Descriptors.Descriptor transformBean( Class<?> clazz, Object dynamicObject, Set<String > exceptedKeys, Options options );

    Descriptors.Descriptor transform( Class<?> clazz, Object dynamicObject, Set<String > exceptedKeys, Options options );

    default Descriptors.Descriptor transform( Class<?> clazz, Object dynamicObject, Set<String > exceptedKeys ) {
        return this.transform( clazz, dynamicObject, exceptedKeys, Options.DefaultOptions );
    }

    DescriptorProtos.FieldDescriptorProto.Type reinterpret( Class<?> type );


    DynamicMessage encode( Descriptors.Descriptor descriptor, Object dynamicObject, Set<String > exceptedKeys, Options options );

    DynamicMessage encodeBean( Descriptors.Descriptor descriptor, Object dynamicObject, Set<String > exceptedKeys, Options options );

    DynamicMessage encode( Descriptors.Descriptor descriptor, Map dynamicObject, Set<String > exceptedKeys, Options options );
}
