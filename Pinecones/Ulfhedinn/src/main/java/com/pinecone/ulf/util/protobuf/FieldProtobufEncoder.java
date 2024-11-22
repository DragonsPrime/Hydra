package com.pinecone.ulf.util.protobuf;

import java.util.Map;
import java.util.Set;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.lang.field.FieldEntity;

public interface FieldProtobufEncoder extends BeanProtobufEncoder {
    Descriptors.Descriptor transform( Map.Entry<String, Object>[] fields, String szEntityName, Set<String > exceptedKeys, Options options );

    DynamicMessage encode( Descriptors.Descriptor descriptor, Map.Entry<String, Object>[] fields, Set<String > exceptedKeys, Options options );

    Descriptors.Descriptor transform(FieldEntity[] fields, String szEntityName, Set<String > exceptedKeys, Options options );

    DynamicMessage encode( Descriptors.Descriptor descriptor, FieldEntity[] fields, Set<String > exceptedKeys, Options options );
}
