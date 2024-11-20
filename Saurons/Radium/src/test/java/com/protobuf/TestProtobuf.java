package com.protobuf;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.mc.JesusChrist;
import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.ulf.util.protobuf.GenericBeanProtobufDecoder;
import com.pinecone.ulf.util.protobuf.GenericBeanProtobufEncoder;
import com.pinecone.ulf.util.protobuf.Options;

class DynamicProtobufBuilder {
    public static Descriptors.Descriptor buildRpcRequestDescriptor() throws Descriptors.DescriptorValidationException {
        DescriptorProtos.DescriptorProto rpcRequestProto = DescriptorProtos.DescriptorProto.newBuilder()
                .setName("RpcRequest")
                .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("method")
                        .setNumber(1)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING))
                .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("payload")
                        .setNumber(2)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES))
                .build();

        DescriptorProtos.FileDescriptorProto fileDescriptorProto = DescriptorProtos.FileDescriptorProto.newBuilder()
                .setName("rpc.proto")
                .addMessageType(rpcRequestProto)
                .build();

        Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[0]);
        return fileDescriptor.findMessageTypeByName("RpcRequest");
    }

    public static Descriptors.Descriptor buildRpcResponseDescriptor() throws Descriptors.DescriptorValidationException {
        DescriptorProtos.DescriptorProto rpcResponseProto = DescriptorProtos.DescriptorProto.newBuilder()
                .setName("RpcResponse")
                .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("code")
                        .setNumber(1)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32))
                .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("message")
                        .setNumber(2)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING))
                .addField(DescriptorProtos.FieldDescriptorProto.newBuilder()
                        .setName("payload")
                        .setNumber(3)
                        .setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES))
                .build();

        DescriptorProtos.FileDescriptorProto fileDescriptorProto = DescriptorProtos.FileDescriptorProto.newBuilder()
                .setName("rpc.proto")
                .addMessageType(rpcResponseProto)
                .build();

        Descriptors.FileDescriptor fileDescriptor = Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new Descriptors.FileDescriptor[0]);
        return fileDescriptor.findMessageTypeByName("RpcResponse");
    }
}

class Appleby extends JesusChrist {
    public Appleby( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Appleby( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
//        RpcRequest request = RpcRequest.newBuilder()
//                .setMethod("haha")
//                .setPayload( ByteString.copyFrom( new byte[]{123} ) )
//                .build();
//
//        byte[] serializedData = request.toByteArray();
//
//        RpcRequest deserializedReq = RpcRequest.parseFrom(serializedData);
//        Debug.trace( deserializedReq.getMethod() );

        //this.testDynamic();

        this.testDynamicUMCT();

        //this.testElementRroto();
    }

    private void testDynamic() throws Exception {
        Descriptors.Descriptor rpcRequestDescriptor = DynamicProtobufBuilder.buildRpcRequestDescriptor();
        Descriptors.Descriptor rpcResponseDescriptor = DynamicProtobufBuilder.buildRpcResponseDescriptor();


        String method  = "echo";
        byte[] payload = "Dragon King".getBytes();

        DynamicMessage request1 = DynamicMessage.newBuilder(rpcRequestDescriptor)
                .setField(rpcRequestDescriptor.findFieldByName("method"), method)
                .setField(rpcRequestDescriptor.findFieldByName("payload"), com.google.protobuf.ByteString.copyFrom(payload))
                .build();

        byte[] rd = request1.toByteArray();


        DynamicMessage request = DynamicMessage.parseFrom(rpcRequestDescriptor, rd);
        String method1 = (String) request.getField(rpcRequestDescriptor.findFieldByName("method"));
        ByteString payload1 = (ByteString) request.getField(rpcRequestDescriptor.findFieldByName("payload"));

        DynamicMessage response = DynamicMessage.newBuilder(rpcResponseDescriptor)
                .setField(rpcResponseDescriptor.findFieldByName("code"), 200)
                .setField(rpcResponseDescriptor.findFieldByName("message"), "Success")
                .setField(rpcResponseDescriptor.findFieldByName("payload"), payload1)
                .build();


        FileOutputStream ofs = new FileOutputStream( "e:/sss.bin" );
        ofs.write( rd );
        ofs.close();
        Debug.greenf( rd );

        Debug.infoSyn( ( (ByteString)DynamicMessage.parseFrom(rpcRequestDescriptor, rd).getField(rpcRequestDescriptor.findFieldByName("payload")) ).toStringUtf8() );
    }

    private void testDynamicUMCT() throws Exception {
        Slave slave = JSON.unmarshal( "{ name:Slave, length:1234, parasite:{ name: parasite, length:20241102 }, atts: { key:val }, li:[1,2,3, 'ssss']," +
                "children: [{ name:SlaveChild, length:137, parasite:{ name: parasitec, length:20241117 }  } ] }", Slave.class );
        Debug.trace( 2, slave );
////
        GenericBeanProtobufEncoder encoder = new GenericBeanProtobufEncoder();
        Descriptors.Descriptor descriptor = encoder.transform( Slave.class, slave, Set.of() );
        Debug.trace( descriptor.getFields() );

        Options options = new Options();
        DynamicMessage message = encoder.encode( descriptor, slave, Set.of(), options );
        Debug.trace( message.getAllFields(), descriptor.findFieldByName( "parasite" ).getMessageType().getFields() );

        byte[] rd = message.toByteArray();
        DynamicMessage unmarshaled = DynamicMessage.parseFrom(descriptor, rd);
        Debug.trace( unmarshaled.getAllFields() );

        GenericBeanProtobufDecoder decoder = new GenericBeanProtobufDecoder();
        Map dm = decoder.decodeMap( descriptor, unmarshaled, Set.of(), options );
        Debug.purplef( dm );

        Slave neo = decoder.decode( Slave.class, descriptor, unmarshaled, Set.of(), options );
        Debug.purplef( neo );



//        Map bear = JSON.unmarshal( "{ name: 'William', force: 320, values: [1,2,3], type: grizzly, trait: { species: mammal } }", Map.class );
//        Debug.trace( bear );
//        Options options = new Options();
//        Descriptors.Descriptor descriptor = encoder.transform( Map.class, bear,  Set.of(), options );
//        Debug.trace( descriptor.getFields() );
//        Debug.trace( descriptor.findFieldByName( "values" ).isRepeated() );
//        Debug.trace( descriptor.findFieldByName( "trait" ).getMessageType().getFields() );
//
//        DynamicMessage message = encoder.encode( descriptor, bear, Set.of(), options );
//        Debug.trace( message.getAllFields(), descriptor.findFieldByName( "trait" ).getMessageType().getFields() );
//        Debug.trace( message.getField( descriptor.findFieldByName( "values" )  ) );
//
//        byte[] rd = message.toByteArray();
//        DynamicMessage unmarshaled = DynamicMessage.parseFrom(descriptor, rd);
//        Debug.trace( unmarshaled.getAllFields() );
//        Debug.trace( unmarshaled.getField( descriptor.findFieldByName( "values" )  ) );
//
//
//        GenericBeanProtobufDecoder decoder = new GenericBeanProtobufDecoder();
//        Map dm = decoder.decodeMap( descriptor, unmarshaled, Set.of(), options );
//        Debug.purplef( dm );


//        FileOutputStream ofs = new FileOutputStream( "e:/sss.bin" );
//        ofs.write( rd );
//        ofs.close();
//        Debug.greenf( rd );

//        Bear bear = JSON.unmarshal( "{ name: 'William', force: 320, values: [1,2,3], type: grizzly }", Bear.class );
//        Debug.trace( bear );
//        Options options = new Options();
//        Descriptors.Descriptor descriptor = encoder.transform( Bear.class, bear,  Set.of(), options );
//        Debug.trace( descriptor.findFieldByName( "values" ).isRepeated() );
//
//        DynamicMessage message = encoder.decode( descriptor, bear, Set.of(), options );
//        Debug.trace( message.getAllFields() );
//        Debug.trace( message.getField( descriptor.findFieldByName( "values" )  ) );


    }

    private void testElementRroto() throws Exception {
        String sz = "miaomiao";

        GenericBeanProtobufEncoder encoder = new GenericBeanProtobufEncoder();
        Descriptors.Descriptor descriptor = encoder.transform( String.class, sz, Set.of() );
        Debug.trace( descriptor.getFields() );

        Options options = new Options();
        DynamicMessage message = encoder.encode( descriptor, sz, Set.of(), options );
        Debug.trace( message.getAllFields() );

        GenericBeanProtobufDecoder decoder = new GenericBeanProtobufDecoder();
        var dm = decoder.decode( String.class, descriptor, message, Set.of(), options );
        Debug.purplef( dm );
    }

}

public class TestProtobuf {
    public static void main( String[] args ) throws Exception {
        //String[] as = args;
        String[] as = new String[]{ "TestWolfMCClient=true" };
        Pinecone.init( (Object...cfg )->{
            Appleby appleby = (Appleby) Pinecone.sys().getTaskManager().add( new Appleby( as, Pinecone.sys() ) );
            appleby.vitalize();
            return 0;
        }, (Object[]) as );
    }
}