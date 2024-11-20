package com.pinecone.ulf.util.protobuf;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.pinecone.framework.system.prototype.Pinenut;

public class PrimitiveWrapper<T> implements Pinenut {
    public final static String FieldName = "value";

    private final T value;

    public PrimitiveWrapper( T value ) {
        if ( value == null || PrimitiveWrapper.isSupportedPrimitive(value) ) {
            this.value = value;
        }
        else {
            throw new IllegalArgumentException( "Unsupported primitive type: " + value.getClass() );
        }
    }

    public T getValue() {
        return this.value;
    }

    public boolean isPrimitive() {
        return this.value == null || PrimitiveWrapper.isSupportedPrimitive(this.value);
    }

    public static boolean isSupportedPrimitive( Object obj ) {
        return PrimitiveWrapper.isSupportedPrimitive( obj.getClass() );
    }

    public static boolean isSupportedPrimitive( Class<?> obj ) {
        return obj.equals( String.class ) || !BeanProtobufEncoder.DefaultEncoder.reinterpret( obj ).equals( DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE ) ;
    }

    public static <T> PrimitiveWrapper<T> wrap( T val ) {
        return new PrimitiveWrapper<>( val );
    }

    public DescriptorProtos.FieldDescriptorProto.Type reinterpret() {
        return BeanProtobufEncoder.DefaultEncoder.reinterpret( this.value.getClass() );
    }

    public Descriptors.Descriptor transform() {
        return PrimitiveWrapper.transform( this.value );
    }

    public static Descriptors.Descriptor transform( Object elem ) {
        try{
            DescriptorProtos.DescriptorProto.Builder descriptorBuilder = DescriptorProtos.DescriptorProto.newBuilder();
            String szEntityName = PrimitiveWrapper.class.getSimpleName() + "_" + elem.getClass().getSimpleName();
            descriptorBuilder.setName( szEntityName );

            DescriptorProtos.FieldDescriptorProto.Type fieldType = BeanProtobufEncoder.DefaultEncoder.reinterpret( elem.getClass() );

            DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                    .setName( PrimitiveWrapper.FieldName )
                    .setNumber( 1 )
                    .setType( fieldType );

            descriptorBuilder.addField( fieldBuilder );

            Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(
                    DescriptorProtos.FileDescriptorProto.newBuilder()
                            .setName( szEntityName + "$FILE" )
                            .addMessageType( descriptorBuilder.build() )
                            .build(),
                    new Descriptors.FileDescriptor[0]);

            return fileDescriptor.findMessageTypeByName( szEntityName );
        }
        catch ( Descriptors.DescriptorValidationException e ) {
            return null;
        }
    }
}
