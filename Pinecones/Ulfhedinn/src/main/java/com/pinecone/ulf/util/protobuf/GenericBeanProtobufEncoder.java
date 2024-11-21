package com.pinecone.ulf.util.protobuf;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.system.stereotype.JavaBeans;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.StringUtils;

public class GenericBeanProtobufEncoder implements BeanProtobufEncoder {
    @Override
    public Descriptors.Descriptor transform( Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        if ( dynamicObject == null ) {
            return null;
        }
        return this.transform( dynamicObject.getClass(), dynamicObject, exceptedKeys, options );
    }

    protected DescriptorProtos.FieldDescriptorProto.Builder transformEntry(
            String key, Object value, int fieldNumber, List<Descriptors.FileDescriptor> dependencies,
            Set<String> exceptedKeys, Options options, String thisKey
    ) {
        DescriptorProtos.FieldDescriptorProto.Type fieldType = value == null
                ? DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING // Default for null values
                : this.reinterpret( value.getClass() );


        DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder;
        if( value != null ) {
            Class<?> elemType = value.getClass();
            if( Collection.class.isAssignableFrom( elemType ) ) {
                Collection co = (Collection) value;
                if( co.isEmpty() ) {
                    fieldType = DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
                }
                else {
                    fieldType = this.reinterpret( co.iterator().next().getClass() );
                }

                fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName( key )
                        .setNumber( fieldNumber )
                        .setType( fieldType )
                        .setLabel( DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED );
            }
            else if( elemType.isArray() ) {
                fieldType = this.reinterpret( elemType.getComponentType() );

                fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName( key )
                        .setNumber( fieldNumber )
                        .setType( this.reinterpret( elemType.getComponentType() ) )
                        .setLabel( DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED );
            }
            else {
                fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName( key )
                        .setNumber( fieldNumber )
                        .setType( fieldType );
            }
        }
        else {
            fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                    .setName( key )
                    .setNumber( fieldNumber )
                    .setType( fieldType );
        }

        if ( fieldType == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE ) {
            Descriptors.Descriptor nestedDescriptor = this.transform0( value.getClass(), thisKey, value, exceptedKeys, options );
            if ( nestedDescriptor != null ) {
                fieldBuilder.setTypeName( nestedDescriptor.getFullName() );
                dependencies.add( nestedDescriptor.getFile() );
            }
        }

        return fieldBuilder;
    }

    protected Descriptors.Descriptor transform0( Map dynamicObject, String thisKey, Set<String> exceptedKeys, Options options ) {
        if ( dynamicObject == null ) {
            return null;
        }
        try {
            String szEntityName = thisKey;
            DescriptorProtos.DescriptorProto.Builder descriptorBuilder = DescriptorProtos.DescriptorProto.newBuilder();
            List<Descriptors.FileDescriptor> dependencies = new ArrayList<>();

            int fieldNumber = 1;

            for ( Object em : dynamicObject.entrySet() ) {
                Map.Entry entry = (Map.Entry) em;

                String key = entry.getKey().toString();
                if ( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                    continue;
                }

                DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder = this.transformEntry(
                        key, entry.getValue(), fieldNumber, dependencies, exceptedKeys, options, szEntityName + "_" + key
                );

                descriptorBuilder.addField( fieldBuilder );
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

    protected Descriptors.FileDescriptor evalMessageType (
            List<Descriptors.FileDescriptor> dependencies, DescriptorProtos.DescriptorProto.Builder descriptorBuilder, String szEntityName, Options options
    ) throws Descriptors.DescriptorValidationException {
        Descriptors.FileDescriptor[] dependencyArray = dependencies.toArray( new Descriptors.FileDescriptor[0] );
        Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(
                DescriptorProtos.FileDescriptorProto.newBuilder()
                        .setName( szEntityName + options.getDescriptorFileExtend() )
                        .addMessageType( descriptorBuilder.build() )
                        .build(),
                dependencyArray);

        return fileDescriptor;
    }

    @Override
    public Descriptors.Descriptor transform( Map dynamicObject, Set<String> exceptedKeys, Options options ) {
        return this.transform0( dynamicObject, "Map_root", exceptedKeys, options );
    }

    @Override
    public DescriptorProtos.FieldDescriptorProto.Builder transform( Collection dynamicObject, Class<? > elementType, String key, int fieldNumber, Options options ) {
        DescriptorProtos.FieldDescriptorProto.Type fieldType = this.reinterpret( elementType );

        return DescriptorProtos.FieldDescriptorProto.newBuilder()
                .setName( key )
                .setNumber( fieldNumber )
                .setType( fieldType )
                .setLabel( DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED );
    }

    @Override
    public Descriptors.Descriptor transformBean( Class<?> clazz, Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        return this.transformBean0( clazz, "", dynamicObject, exceptedKeys, options );
    }

    protected Descriptors.Descriptor transformBean0( Class<?> clazz, String thisKey, Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        if ( clazz == null ) {
            return null;
        }

        try {
            String szEntityName = options.formatFileDescType( clazz );
            DescriptorProtos.DescriptorProto.Builder descriptorBuilder = DescriptorProtos.DescriptorProto.newBuilder();
            descriptorBuilder.setName( szEntityName );

            List<Descriptors.FileDescriptor> dependencies = new ArrayList<>();
            boolean includeSuperClass = clazz.getClassLoader() != null;
            Method[] methods = includeSuperClass ? clazz.getMethods() : clazz.getDeclaredMethods();

            int fieldNumber = 1;
            for( int i = 0; i < methods.length; ++i ) {
                try {
                    Method method = methods[i];
                    if ( Modifier.isPublic( method.getModifiers() ) ) {
                        String key = JavaBeans.getGetterMethodKeyName( method );
                        if( !StringUtils.isEmpty( key ) ) {
                            if ( Character.isUpperCase( key.charAt(0) ) && method.getParameterTypes().length == 0 ) {
                                key = JavaBeans.methodKeyNameLowerCaseNormalize( key );

                                if( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                                    continue;
                                }

                                Class<?> elemRetType = method.getReturnType();
                                DescriptorProtos.FieldDescriptorProto.Type fieldType = this.reinterpret( elemRetType );

                                DescriptorProtos.FieldDescriptorProto.Builder fieldBuilder;
                                if( Collection.class.isAssignableFrom( elemRetType ) ) {
                                    Type gt = method.getGenericReturnType();
                                    String[] genericTypeNames = ReflectionUtils.extractGenericClassNames( gt.getTypeName() );
                                    if( genericTypeNames != null && genericTypeNames.length > 0 ) {
                                        String genericTypeName = genericTypeNames[ 0 ];

                                        if( !genericTypeName.equals( "?" ) && !genericTypeName.equals( Object.class.getSimpleName() ) ) {
                                            try{
                                                fieldType = this.reinterpret(  this.getClass().getClassLoader().loadClass( genericTypeName ) );
                                            }
                                            catch ( ClassNotFoundException e ) {
                                                continue;
                                            }
                                        }
                                    }

                                    fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                                            .setName( key )
                                            .setNumber( fieldNumber )
                                            .setType( fieldType )
                                            .setLabel( DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED );
                                }
                                else if( elemRetType.isArray() ) {
                                    fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                                            .setName( key )
                                            .setNumber( fieldNumber )
                                            .setType( this.reinterpret( elemRetType.getComponentType() ) )
                                            .setLabel( DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED );
                                }
                                else {
                                    fieldBuilder = DescriptorProtos.FieldDescriptorProto.newBuilder()
                                            .setName( key )
                                            .setNumber( fieldNumber )
                                            .setType( fieldType );
                                }
                                fieldNumber++;


                                if ( fieldType == DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE ) {
                                    Class<?> nestedClass = method.getReturnType();
                                    Object dyChild;
                                    try{
                                        method.setAccessible( true );
                                        dyChild = method.invoke( dynamicObject );
                                    }
                                    catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
                                        dyChild = null;
                                    }

                                    Descriptors.Descriptor nestedDescriptor = this.transform0( nestedClass, szEntityName + "_" + key, dyChild, exceptedKeys, options );
                                    if( nestedDescriptor == null ) {
                                        continue;
                                    }
                                    fieldBuilder.setTypeName( nestedDescriptor.getFullName() );
                                    dependencies.add( nestedDescriptor.getFile() );
                                }

                                descriptorBuilder.addField( fieldBuilder );
                            }
                        }
                    }
                }
                catch ( Exception ignore ) {
                    ignore.printStackTrace();
                    // Do nothing.
                }
            }

            Descriptors.FileDescriptor fileDescriptor = this.evalMessageType( dependencies, descriptorBuilder, szEntityName, options );
            return fileDescriptor.findMessageTypeByName( szEntityName );
        }
        catch ( Descriptors.DescriptorValidationException e ) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Descriptors.Descriptor transform( Class<?> clazz, Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        if( dynamicObject instanceof Map ) {
            return this.transform( (Map) dynamicObject, exceptedKeys, options );
        }

        return this.transformBean( clazz, dynamicObject, exceptedKeys, options );
    }

    protected Descriptors.Descriptor transform0( Class<?> clazz, String thisKey, Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        if( dynamicObject instanceof Map ) {
            return this.transform0( (Map) dynamicObject, thisKey, exceptedKeys, options );
        }

        return this.transformBean0( clazz, thisKey, dynamicObject, exceptedKeys, options );
    }


    @Override
    public DescriptorProtos.FieldDescriptorProto.Type reinterpret( Class<?> type ) {
        if ( type == int.class || type == Integer.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
        }
        else if ( type == long.class || type == Long.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64;
        }
        else if ( type == float.class || type == Float.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT;
        }
        else if ( type == double.class || type == Double.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE;
        }
        else if ( type == String.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
        }
        else if ( type == boolean.class || type == Boolean.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL;
        }
        else if ( type == byte[].class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES;
        }
        else if ( type == short.class || type == Short.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
        }
        else if ( type == byte.class || type == Byte.class ) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
        }
        else {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE;
        }
    }

    @Override
    public DynamicMessage encode( Descriptors.Descriptor descriptor, Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        if( PrimitiveWrapper.isSupportedPrimitive( dynamicObject.getClass() ) ) {
            dynamicObject = PrimitiveWrapper.wrap( dynamicObject );
        }
        else if( dynamicObject instanceof Map ) {
            return this.encode( descriptor, (Map) dynamicObject, exceptedKeys, options );
        }

        return this.encodeBean( descriptor, dynamicObject, exceptedKeys, options );
    }

    @Override
    public DynamicMessage encodeBean( Descriptors.Descriptor descriptor, Object dynamicObject, Set<String> exceptedKeys, Options options ) {
        if ( descriptor == null || dynamicObject == null ) {
            return null;
        }

        DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder( descriptor );

        for ( Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields() ) {
            try {
                String fieldName = fieldDescriptor.getName();

                // Skip excluded keys
                if ( exceptedKeys != null && exceptedKeys.contains( fieldName ) ) {
                    continue;
                }

                try {

                    String szGetterMethod = JavaBeans.MethodMajorKeyGet + JavaBeans.methodKeyNameUpperCaseNormalize( fieldName );
                    Method         getter = dynamicObject.getClass().getMethod( szGetterMethod );
                    if ( getter != null ) {
                        Object value = getter.invoke( dynamicObject );

                        if ( value != null ) {
                            if ( fieldDescriptor.isRepeated() ) {
                                if ( value instanceof Collection<?> ) {
                                    for ( Object item : (Collection<?>) value ) {
                                        messageBuilder.addRepeatedField( fieldDescriptor, this.reinterpretFieldValue( item, fieldDescriptor.getType() ) );
                                    }
                                }
                                else if ( value.getClass().isArray() ) {
                                    for ( Object item : (Object[]) value ) {
                                        messageBuilder.addRepeatedField( fieldDescriptor, this.reinterpretFieldValue( item, fieldDescriptor.getType() ) );
                                    }
                                }
                                else {
                                    throw new IllegalArgumentException( "Expected a Collection for repeated field: " + fieldName );
                                }
                            }
                            else {
                                this.encodeElement( fieldDescriptor, messageBuilder, value, exceptedKeys, options );
                            }
                        }
                    }
                }
                catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ignore ) {
                    //ignore.printStackTrace();
                }
            }
            catch ( Exception e ) {
                // Log and continue processing other fields
                e.printStackTrace();
            }
        }

        return messageBuilder.build();
    }

    protected void encodeElement( Descriptors.FieldDescriptor fieldDescriptor, DynamicMessage.Builder messageBuilder, Object value, Set<String> exceptedKeys, Options options ) {
        if ( fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE ) {
            Descriptors.Descriptor nestedDescriptor = fieldDescriptor.getMessageType();
            messageBuilder.setField( fieldDescriptor, this.encode( nestedDescriptor, value, exceptedKeys, options ) );
        }
        else {
            messageBuilder.setField( fieldDescriptor, this.reinterpretFieldValue( value, fieldDescriptor.getType() ) );
        }
    }

    @Override
    public DynamicMessage encode( Descriptors.Descriptor descriptor, Map dynamicObject, Set<String> exceptedKeys, Options options ) {
        if ( descriptor == null || dynamicObject == null ) {
            return null;
        }

        try {
            DynamicMessage.Builder messageBuilder = DynamicMessage.newBuilder( descriptor );

            for ( Object em : dynamicObject.entrySet() ) {
                Map.Entry entry = (Map.Entry) em;

                this.encodeEntry( descriptor, entry.getKey().toString(), entry.getValue(), messageBuilder, exceptedKeys, options );
            }

            return messageBuilder.build();
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    public void encodeEntry( Descriptors.Descriptor descriptor, String key, Object value, DynamicMessage.Builder messageBuilder, Set<String> exceptedKeys, Options options ) {
        if ( exceptedKeys != null && exceptedKeys.contains( key ) ) {
            return;
        }

        Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName( key );
        if ( fieldDescriptor == null ) {
            return;
        }

        if ( value == null ) {
            if ( fieldDescriptor.isRepeated() ) {
                messageBuilder.setField( fieldDescriptor, new ArrayList<>() );
            }
            else {
                messageBuilder.clearField( fieldDescriptor );
            }
        }
        else if ( fieldDescriptor.isRepeated() ) {
            List<Object> values = new ArrayList<>();
            if ( value instanceof Collection ) {
                for ( Object item : (Collection<?>) value ) {
                    values.add( this.reinterpretFieldValue( item, fieldDescriptor.getType() ) );
                }
            }
            else if ( value.getClass().isArray() ) {
                for ( int i = 0; i < Array.getLength( value ); i++ ) {
                    values.add( this.reinterpretFieldValue( Array.get( value, i ), fieldDescriptor.getType() ) );
                }
            }
            messageBuilder.setField( fieldDescriptor, values );
        }
        else {
            this.encodeElement( fieldDescriptor, messageBuilder, value, exceptedKeys, options );
        }
    }

    protected Object reinterpretFieldValue(Object value, Descriptors.FieldDescriptor.Type fieldType ) {
        switch ( fieldType ) {
            case SINT32:
            case SFIXED32:
            case INT32: {
                return ((Number) value).intValue();
            }
            case INT64:
            case SINT64:
            case SFIXED64: {
                return ((Number) value).longValue();
            }
            case FLOAT: {
                return ((Number) value).floatValue();
            }
            case DOUBLE: {
                return ((Number) value).doubleValue();
            }
            case STRING: {
                return value.toString();
            }
            case BOOL: {
                return (Boolean) value;
            }
            case BYTES: {
                return ByteString.copyFrom( (byte[]) value );
            }
            default: {
                return value;
            }
        }
    }

}
