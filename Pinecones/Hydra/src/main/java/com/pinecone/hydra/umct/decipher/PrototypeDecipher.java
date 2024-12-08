package com.pinecone.hydra.umct.decipher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umct.protocol.compiler.CompilerEncoder;

public class PrototypeDecipher implements HeaderDecipher {
    protected volatile static Descriptors.Descriptor PathDescriptor = null;

    protected static Descriptors.FieldDescriptor PathFieldDescriptor = null;

    protected String           mszServicePathKey;

    protected CompilerEncoder  mCompilerEncoder;

    public PrototypeDecipher( String szServicePathKey, CompilerEncoder encoder ) {
        this.mszServicePathKey = szServicePathKey;
        this.mCompilerEncoder  = encoder;
    }

    @Override
    public String getServicePath( Object that ) {
        try{
            Descriptors.Descriptor des = PrototypeDecipher.getPathDescriptor( this.mszServicePathKey );
            DynamicMessage ms = DynamicMessage.parseFrom( des, (byte[]) that );
            return (String) ms.getField( PrototypeDecipher.PathFieldDescriptor );
        }
        catch ( InvalidProtocolBufferException e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }

    @Override
    public Object eval( Object that, Object descriptor, String key ) {
        byte[] data = (byte[]) that;
        Descriptors.Descriptor messageDescriptor = (Descriptors.Descriptor) descriptor;

        try {
            DynamicMessage message = DynamicMessage.parseFrom( messageDescriptor, data );
            Descriptors.FieldDescriptor fieldDescriptor = messageDescriptor.findFieldByName( key );

            if ( fieldDescriptor == null ) {
                throw new IllegalArgumentException( "Field '" + key + "' not found in the descriptor" );
            }

            return message.getField(fieldDescriptor);
        }
        catch ( InvalidProtocolBufferException e ) {
            throw new IllegalArgumentException( e );
        }
    }

    @Override
    public Collection<Object > values( Object that, Object descriptor ) {
        byte[] data = (byte[]) that;
        Descriptors.Descriptor messageDescriptor = (Descriptors.Descriptor) descriptor;

        try {
            DynamicMessage message = DynamicMessage.parseFrom( messageDescriptor, data );
            Collection<Object > fieldValues = new ArrayList<>();

            int i = 0;
            for ( Descriptors.FieldDescriptor fieldDescriptor : messageDescriptor.getFields() ) {
                if ( i != 0 ) {
                    Object value = message.getField( fieldDescriptor );
                    fieldValues.add( value );
                }
                ++i;
            }
            return fieldValues;
        }
        catch ( InvalidProtocolBufferException e ) {
            throw new IllegalArgumentException( e );
        }
    }

    @Override
    public Object[] evals( Object that, Object descriptor, List<String> keys ) {
        byte[] data = (byte[]) that;
        Descriptors.Descriptor messageDescriptor = (Descriptors.Descriptor) descriptor;

        try {
            DynamicMessage message = DynamicMessage.parseFrom( messageDescriptor, data );
            Object[] results = new Object[ keys.size() ];

            for ( int i = 1; i < keys.size(); ++i ) {
                String key = keys.get(i);
                Descriptors.FieldDescriptor fieldDescriptor = messageDescriptor.findFieldByName(key);

                if ( fieldDescriptor == null ) {
                    results[i] = null; // Field not found
                }
                else {
                    results[i] = message.getField(fieldDescriptor);
                }
            }

            return results;
        }
        catch ( InvalidProtocolBufferException e ) {
            throw new IllegalArgumentException( e );
        }
    }

    @Override
    public UMCMessage assembleReturnMsg( Object that, Object descriptor ) {
        if ( that instanceof UMCMessage ) {
            return (UMCMessage) that;
        }
        if ( that == null ) {
            return new UlfInformMessage( null, ExtraEncode.Prototype );
        }

        Descriptors.Descriptor retDes = (Descriptors.Descriptor) descriptor;
        DynamicMessage retMsg = this.mCompilerEncoder.getEncoder().encode( retDes, that, this.mCompilerEncoder.getExceptedKeys(), this.mCompilerEncoder.getOptions() );
        return new UlfInformMessage( retMsg.toByteArray() ); // TODO, Transfer
    }

    public static Descriptors.Descriptor getPathDescriptor( String fieldName ) {
        if ( PrototypeDecipher.PathDescriptor == null ) {
            synchronized ( PrototypeDecipher.class ) {
                if ( PrototypeDecipher.PathDescriptor == null ) {
                    PrototypeDecipher.PathDescriptor      = PrototypeDecipher.createPathDescriptor( "PathDescriptor", fieldName );
                    PrototypeDecipher.PathFieldDescriptor = PrototypeDecipher.PathDescriptor.findFieldByName( fieldName );
                }
            }
        }

        return PrototypeDecipher.PathDescriptor;
    }

    public static Descriptors.Descriptor createPathDescriptor( String messageName, String fieldName ) {
        try {
            DescriptorProtos.FieldDescriptorProto fieldDescriptorProto = DescriptorProtos.FieldDescriptorProto.newBuilder()
                    .setName(fieldName)
                    .setNumber(1)
                    .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING)
                    .build();

            DescriptorProtos.DescriptorProto descriptorProto = DescriptorProtos.DescriptorProto.newBuilder()
                    .setName(messageName)
                    .addField(fieldDescriptorProto)
                    .build();

            DescriptorProtos.FileDescriptorProto fileDescriptorProto = DescriptorProtos.FileDescriptorProto.newBuilder()
                    .setName(messageName + ".proto")
                    .addMessageType(descriptorProto)
                    .build();

            Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(
                    fileDescriptorProto, new Descriptors.FileDescriptor[]{});

            return fileDescriptor.findMessageTypeByName(messageName);
        }
        catch ( Descriptors.DescriptorValidationException e ) {
            throw new ProxyProvokeHandleException( "Failed to create descriptor", e );
        }
    }
}
