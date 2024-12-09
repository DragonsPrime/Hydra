package com.pinecone.ulf.util.protobuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.lang.field.FieldEntity;

public class GenericFieldProtobufEncoder extends GenericBeanProtobufEncoder implements FieldProtobufEncoder {
    @Override
    public Descriptors.Descriptor transform( Map.Entry<String, Object>[] fields, String szEntityName, Set<String> exceptedKeys, Options options ) {
        if ( fields == null ) {
            return null;
        }
        try {
            DescriptorProtos.DescriptorProto.Builder descriptorBuilder = DescriptorProtos.DescriptorProto.newBuilder();
            List<Descriptors.FileDescriptor> dependencies = new ArrayList<>();

            int fieldNumber = 1;

            for ( Map.Entry entry : fields ) {
                String key = entry.getKey().toString();
                if ( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                    continue;
                }

                descriptorBuilder.addField( this.transformEntry(
                        key, entry.getValue(), null, fieldNumber, dependencies, exceptedKeys, options, szEntityName + "_" + key
                ) );
                ++fieldNumber;
            }

            descriptorBuilder.setName( szEntityName );
            Descriptors.FileDescriptor fileDescriptor = this.evalMessageType( dependencies, descriptorBuilder, szEntityName, options );
            return fileDescriptor.findMessageTypeByName( szEntityName );
        }
        catch ( Descriptors.DescriptorValidationException e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Descriptors.Descriptor transform( FieldEntity[] fields, String szEntityName, Set<String> exceptedKeys, Options options ) {
        if ( fields == null ) {
            return null;
        }
        try {
            DescriptorProtos.DescriptorProto.Builder descriptorBuilder = DescriptorProtos.DescriptorProto.newBuilder();
            List<Descriptors.FileDescriptor> dependencies = new ArrayList<>();

            int fieldNumber = 1;

            for ( FieldEntity entry : fields ) {
                String key = entry.getName();
                if ( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                    continue;
                }

                descriptorBuilder.addField( this.transformEntry(
                        key, entry.getValue(), entry.getType(), fieldNumber, dependencies, exceptedKeys, options, szEntityName + "_" + key
                ) );
                ++fieldNumber;
            }

            descriptorBuilder.setName( szEntityName );
            Descriptors.FileDescriptor fileDescriptor = this.evalMessageType( dependencies, descriptorBuilder, szEntityName, options );
            return fileDescriptor.findMessageTypeByName( szEntityName );
        }
        catch ( Descriptors.DescriptorValidationException e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DynamicMessage encode( Descriptors.Descriptor descriptor, Map.Entry<String, Object>[] fields, Set<String> exceptedKeys, Options options ) {
        if ( descriptor == null || fields == null ) {
            return null;
        }

        try {
            DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder( descriptor );

            for ( Map.Entry entry : fields ) {
                this.encodeEntry( descriptor, entry.getKey().toString(), entry.getValue(), messageBuilder, exceptedKeys, options );
            }

            return messageBuilder.build();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DynamicMessage encode( Descriptors.Descriptor descriptor, FieldEntity[] fields, Set<String> exceptedKeys, Options options ) {
        if ( descriptor == null || fields == null ) {
            return null;
        }

        try {
            DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder( descriptor );

            for ( FieldEntity entry : fields ) {
                this.encodeEntry( descriptor, entry.getName(), entry.getValue(), messageBuilder, exceptedKeys, options );
            }

            return messageBuilder.build();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
}
