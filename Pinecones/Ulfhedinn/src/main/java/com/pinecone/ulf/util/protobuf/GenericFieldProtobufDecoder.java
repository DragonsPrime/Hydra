package com.pinecone.ulf.util.protobuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.unit.KeyValue;

public class GenericFieldProtobufDecoder extends GenericBeanProtobufDecoder implements FieldProtobufDecoder {

    @Override
    public Map.Entry<String, Object>[] decodeEntries( Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String> exceptedKeys, Options options ) {
        if ( descriptor == null || dynamicMessage == null ) {
            return null;
        }

        List<Descriptors.FieldDescriptor> fieldDescriptors = descriptor.getFields();

        @SuppressWarnings( "unchecked" )
        Map.Entry<String, Object>[] result = new Map.Entry[ fieldDescriptors.size() ];

        int i = 0;
        for ( Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields() ) {
            try {
                String fieldName = fieldDescriptor.getName();

                // Skip excluded keys
                if ( exceptedKeys != null && exceptedKeys.contains( fieldName ) ) {
                    continue;
                }

                Object value = dynamicMessage.getField( fieldDescriptor );

                if ( value != null ) {
                    if ( fieldDescriptor.isRepeated() ) {
                        List<?> values = (List<?>) value;
                        List<Object> decodedValues = new ArrayList<>();
                        for ( Object item : values ) {
                            decodedValues.add( this.decodeFieldValue( fieldDescriptor, item, options ) );
                        }

                        result[ i ] = new KeyValue<>( fieldName, decodedValues );
                    }
                    else if ( fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE ) {
                        Descriptors.Descriptor nestedDescriptor = fieldDescriptor.getMessageType();
                        result[ i ] = new KeyValue<>( fieldName, this.decodeMap( LinkedHashMap.class, nestedDescriptor, (DynamicMessage) value, exceptedKeys, options ) );
                    }
                    else {
                        result[ i ] = new KeyValue<>( fieldName, this.decodeFieldValue( fieldDescriptor, value, options ) );
                    }
                }

                ++i;
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public void decodeEntries( FieldEntity[] entities, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String> exceptedKeys, Options options ) {
        this.decodeEntries0( entities, descriptor, dynamicMessage, exceptedKeys, options, false );
    }


    @Override
    public Object[] decodeValues( FieldEntity[] entities, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String> exceptedKeys, Options options ) {
        return this.decodeEntries0( entities, descriptor, dynamicMessage, exceptedKeys, options, true );
    }

    protected Object[] decodeEntries0( FieldEntity[] entities, Descriptors.Descriptor descriptor, DynamicMessage dynamicMessage, Set<String> exceptedKeys, Options options, boolean bEvalValue ) {
        if ( descriptor == null || dynamicMessage == null || entities == null ) {
            return null;
        }

        try {
            int i = 0;
            Object[] vals = null;
            if( bEvalValue ) {
                vals = new Object[ entities.length ];
            }

            for ( Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields() ) {
                String fieldName = fieldDescriptor.getName();

                if ( exceptedKeys != null && exceptedKeys.contains( fieldName ) ) {
                    continue;
                }

                Object value = dynamicMessage.getField( fieldDescriptor );

                if ( value != null ) {
                    FieldEntity entity = entities[ i ];

                    if ( fieldDescriptor.isRepeated() ) {
                        List<Object> decodedValues = new ArrayList<>();
                        List<?> values = (List<?>) value;
                        for ( Object item : values ) {
                            decodedValues.add( this.decodeFieldValue( fieldDescriptor, item, options ) );
                        }

                        entity.setValue( decodedValues );
                    }
                    else if ( fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE ) {
                        Descriptors.Descriptor nestedDescriptor = fieldDescriptor.getMessageType();
                        Object nestedBean;
                        Class<?> nestedType = entity.getType();
                        if( Map.class.isAssignableFrom( nestedType ) ) {
                            if( nestedType.isInterface() && Map.class.isAssignableFrom( nestedType ) ) {
                                nestedType = options.getDefaultMapType();
                            }
                            nestedBean = this.decodeMap( nestedType, nestedDescriptor, (DynamicMessage) value, exceptedKeys, options );
                        }
                        else {
                            nestedBean = this.decode( nestedType, nestedDescriptor, (DynamicMessage) value, exceptedKeys, options );
                        }

                        entity.setValue( nestedBean );
                    }
                    else {
                        entity.setValue( this.decodeFieldValue( fieldDescriptor, value, options ) );
                    }

                    if( bEvalValue ) {
                        vals[ i ] = entity.getValue();
                    }
                }
                ++i;
            }

            return vals;
        }
        catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

}
