package com.protobuf;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umct.protocol.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.protocol.compiler.ClassDigest;
import com.pinecone.hydra.umct.protocol.compiler.MethodDigest;
import com.pinecone.hydra.umct.protocol.compiler.MethodPrototype;
import com.pinecone.hydra.umct.protocol.function.ArgumentRequest;
import com.pinecone.hydra.umct.protocol.function.GenericArgumentRequest;
import com.pinecone.ulf.util.protobuf.GenericBeanProtobufDecoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.Options;

import javassist.ClassPool;

public class RedRaccoon implements Raccoon {
    //@Override
    public String scratch1( String target, int time ) {
        try{
            Method[] methods = Raccoon.class.getMethods();
            GenericArgumentRequest request = new GenericArgumentRequest( Raccoon.class.getName(), methods[0].getParameterTypes() );

            GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
            GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
            Options options = new Options();

            FieldEntity[] types = request.getSegments();
            Descriptors.Descriptor descriptor = encoder.transform( types, "Args", Set.of(), options );
            Debug.trace( descriptor.getFields() );

            request.setField( 0, target );
            request.setField( 1, time );
            DynamicMessage message = encoder.encode( descriptor, types, Set.of(), options );
            byte[] mb = message.toByteArray();
            message = DynamicMessage.parseFrom( descriptor, mb );

            request = new GenericArgumentRequest( Raccoon.class.getName(), methods[0].getParameterTypes() );
            decoder.decodeEntries( request.getSegments(), descriptor, message, Set.of(), options );



            Descriptors.Descriptor retDes = encoder.transform( String.class, null, Set.of() );
            Debug.trace( retDes.getFields() );
            DynamicMessage retMsg = encoder.encode( retDes, request.getField(0).getValue(), Set.of(), options );
            DynamicMessage retDy = DynamicMessage.parseFrom( retDes, retMsg.toByteArray() );

            String dm = decoder.decode( String.class, retDes, retDy, Set.of(), options );
            Debug.info(dm);
            return "scratch " + dm;
        }
        catch ( InvalidProtocolBufferException e ) {
            return null;
        }
    }

    @Override
    public String scratch( String target, int time ) {
        try{
            BytecodeIfacCompiler inspector = new BytecodeIfacCompiler( ClassPool.getDefault() );
            List<MethodDigest> digests = inspector.compile( Raccoon.class, false ).getMethodDigests();
            MethodPrototype methodPrototype = (MethodPrototype)digests.get(0);
            Descriptors.Descriptor argDes = methodPrototype.getArgumentsDescriptor();
            Descriptors.Descriptor retDes = methodPrototype.getReturnDescriptor();




            GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
            GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
            Options options = new Options();



            ArgumentRequest request = methodPrototype.conformRequest( new Object[] { target, time } );

            FieldEntity[] types = request.getSegments();
            Debug.trace( argDes.getFields() );

            DynamicMessage message = encoder.encode( argDes, types, Set.of(), options );
            byte[] mb = message.toByteArray();
            message = DynamicMessage.parseFrom( argDes, mb );

            request = methodPrototype.conformRequest();
            decoder.decodeEntries( request.getSegments(), argDes, message, Set.of(), options );


            Debug.trace( retDes.getFields() );
            DynamicMessage retMsg = encoder.encode( retDes, request.getField(0).getValue(), Set.of(), options );
            DynamicMessage retDy = DynamicMessage.parseFrom( retDes, retMsg.toByteArray() );

            String dm = decoder.decode( String.class, retDes, retDy, Set.of(), options );
            Debug.info(dm);
            return "scratch " + dm;
        }
        catch ( InvalidProtocolBufferException e ) {
            return null;
        }
    }
}
