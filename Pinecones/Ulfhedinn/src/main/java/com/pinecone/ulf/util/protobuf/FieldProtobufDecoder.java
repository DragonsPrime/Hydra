package com.pinecone.ulf.util.protobuf;

import java.util.Map;
import java.util.Set;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.lang.field.FieldEntity;

public interface FieldProtobufDecoder extends BeanProtobufDecoder {
    Map.Entry<String, Object>[] decodeEntries( Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options );

    void decodeEntries( FieldEntity[] entities, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options );

    Object[] decodeValues( FieldEntity[] entities, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String > exceptedKeys, Options options );
}
