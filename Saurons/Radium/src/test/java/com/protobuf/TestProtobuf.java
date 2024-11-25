package com.protobuf;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.mc.JesusChrist;
import com.pinecone.Pinecone;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.lang.field.GenericStructure;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.pinecone.hydra.umc.wolfmc.server.WolfMCServer;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.protocol.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.protocol.compiler.GenericIfaceInspector;
import com.pinecone.hydra.umct.protocol.function.GenericArgumentRequest;
import com.pinecone.ulf.util.lang.GenericPreloadClassInspector;
import com.pinecone.ulf.util.protobuf.GenericBeanProtobufDecoder;
import com.pinecone.ulf.util.protobuf.GenericBeanProtobufEncoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.Options;
import com.sauron.radium.messagron.Messagron;

import io.netty.channel.ChannelHandlerContext;
import javassist.ClassPool;

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

        //this.testDynamicUMCT();

        //this.testElementRroto();

        //this.testFieldEntry();

        //this.testReflect();

        //this.testManualRPCServer();

        //this.testManualRPCClient();

        //this.testStructure();

        this.testIfacInspector();
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

    private void testFieldEntry() throws Exception {
        GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();


        Map bear = JSON.unmarshal( "{ name: 'William', force: 320, values: [1,2,3], type: grizzly, trait: { species: mammal } }", Map.class );
        Debug.trace( bear );
        Options options = new Options();

        FieldEntity[] entities = FieldEntity.from( bear );


        Descriptors.Descriptor descriptor = encoder.transform( entities, "Args", Set.of(), options );
        Debug.trace( descriptor.getFields() );
        Debug.trace( descriptor.findFieldByName( "values" ).isRepeated() );
        Debug.trace( descriptor.findFieldByName( "trait" ).getMessageType().getFields() );

        DynamicMessage message = encoder.encode( descriptor, entities, Set.of(), options );
        Debug.trace( message.getAllFields(), descriptor.findFieldByName( "trait" ).getMessageType().getFields() );
        Debug.trace( message.getField( descriptor.findFieldByName( "values" )  ) );

        byte[] rd = message.toByteArray();
        DynamicMessage unmarshaled = DynamicMessage.parseFrom(descriptor, rd);
        Debug.trace( unmarshaled.getAllFields() );
        Debug.trace( unmarshaled.getField( descriptor.findFieldByName( "values" )  ) );



        FieldEntity[] types = FieldEntity.typeFrom( bear );

        GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
        Map.Entry<String, Object>[] kvs = decoder.decodeEntries( descriptor, unmarshaled, Set.of(), options );
        Debug.trace( kvs );

        decoder.decodeEntries( types, descriptor, unmarshaled, Set.of(), options );
        Debug.trace( types[4].getType() );

        Object[] vals = decoder.decodeValues( types, descriptor, unmarshaled, Set.of(), options );
        Debug.trace( vals );
    }

    private void testReflect() throws Exception {
        GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
        Options options = new Options();

        GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();

        Method[] methods = Raccoon.class.getMethods();
        for ( Method method : methods ) {
            FieldEntity[] types = FieldEntity.from( method.getParameterTypes() );
            Debug.trace( types );

            types[ 0 ].setValue( "red_raccoon" );
            types[ 1 ].setValue( 12345L );

            Descriptors.Descriptor descriptor = encoder.transform( types, "Args", Set.of(), options );
            Debug.trace( descriptor.getFields() );

            DynamicMessage message = encoder.encode( descriptor, types, Set.of(), options );
            Debug.trace( message.getAllFields() );

            types = FieldEntity.from( method.getParameterTypes() );
            Object[] vals = decoder.decodeValues( types, descriptor, message, Set.of(), options );
            Debug.trace( vals );
        }
    }

    private void testManualRPCServer() throws Exception {
        Messagron messagron = new Messagron( "", this, new JSONMaptron() );
        WolfMCServer wolf = new WolfMCServer( "", this, new JSONMaptron("{host: \"0.0.0.0\",\n" +
                "port: 5777, SocketTimeout: 800, KeepAliveTimeout: 3600, MaximumConnections: 1e6}") );

        Method[] methods = Raccoon.class.getMethods();
        Class<? > retType = methods[0].getReturnType();
        GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
        Options options = new Options();

        String sz = "xixihaha";
        Descriptors.Descriptor descriptor = encoder.transform( String.class, sz, Set.of() );
        Debug.trace( descriptor.getFields() );

        DynamicMessage message = encoder.encode( descriptor, sz, Set.of(), options );
        Debug.trace( message.getAllFields() );
        wolf.apply( new UlfAsyncMsgHandleAdapter() {
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                UlfInformMessage mc = (UlfInformMessage) rawMsg;
                byte[]bytes = (byte[]) mc.getHead().getExtraHead();


                Method[] methods = Raccoon.class.getMethods();
                FieldEntity[] types = FieldEntity.from( methods[0].getParameterTypes() );
                Debug.trace( types );

                GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
                Options options = new Options();

                Descriptors.Descriptor descriptor = encoder.transform( types, "Args", Set.of(), options );
                Debug.trace( descriptor.getFields() );

                DynamicMessage unmarshaled = DynamicMessage.parseFrom(descriptor, bytes);
                GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
                types = FieldEntity.from( methods[0].getParameterTypes() );
                Object[] vals = decoder.decodeValues( types, descriptor, unmarshaled, Set.of(), options );
                Debug.trace( vals );


                String sz = vals[0].toString();
                Descriptors.Descriptor retDes = encoder.transform( String.class, sz, Set.of() );
                Debug.trace( retDes.getFields() );

                DynamicMessage retMsg = encoder.encode( retDes, sz, Set.of(), options );
                block.getTransmit().sendMsg(new UlfInformMessage(retMsg.toByteArray()));

            }
        });

        wolf.execute();

        this.getTaskManager().add( wolf );
        //this.getTaskManager().syncWaitingTerminated();
    }


    private void testManualRPCClient() throws Exception {
        Messagron servtron = new Messagron( "", this, new JSONMaptron( "{\n" +
                "  \"Engine\"            : \"com.sauron.radium.messagron.Messagron\",\n" +
                "  \"Enable\"            : true,\n" +
                "  \"ExpressFactory\"    : \"com.pinecone.framework.util.lang.GenericDynamicFactory\",\n" +
                "\n" +
                "  \"Expresses\"         : {\n" +
                "    \"WolfMCExpress\": {\n" +
                "      \"Engine\": \"com.pinecone.hydra.umct.WolfMCExpress\"\n" +
                "    }\n" +
                "  }\n" +
                "}" ) );

        WolfMCClient wolf = new WolfMCClient( "", this, this.getMiddlewareManager().getMiddlewareConfig().queryJSONObject( "Messagers.Messagers.WolfMCKingpin" ) );
        wolf.apply( new WolfMCExpress( servtron ) ).execute();




        Method[] methods = Raccoon.class.getMethods();
        FieldEntity[] types = FieldEntity.from( methods[0].getParameterTypes() );
        Debug.trace( types );

        GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
        Options options = new Options();

        Descriptors.Descriptor descriptor = encoder.transform( types, "Args", Set.of(), options );
        Debug.trace( descriptor.getFields() );

        types[0].setValue("fuck you");
        types[1].setValue(2024);
        DynamicMessage message = encoder.encode( descriptor, types, Set.of(), options );
        Debug.trace( message.getAllFields() );



        Debug.sleep( 500 );
        UMCMessage retMsg = wolf.sendSyncMsg(new UlfInformMessage(message.toByteArray()));
        if(retMsg instanceof UlfInformMessage) {
            Debug.trace(retMsg.getHead().getExtraHead());


            Descriptors.Descriptor retDes = encoder.transform( String.class, "", Set.of() );
            Debug.trace( retDes.getFields() );

            DynamicMessage retDy = DynamicMessage.parseFrom( retDes, (byte[])retMsg.getHead().getExtraHead() );
            GenericBeanProtobufDecoder decoder = new GenericBeanProtobufDecoder();
            var dm = decoder.decode( String.class, retDes, retDy, Set.of(), options );
            Debug.info(dm);
        }
        this.getTaskManager().add( wolf );
        this.getTaskManager().syncWaitingTerminated();
    }


    protected void testStructure() throws Exception {
        GenericStructure structure = new GenericStructure( "test.red", 3 );
        structure.setDataField( 0, "name", "test" );
        structure.setDataField( 1, "t1", "v1" );
        structure.setDataField( 2, "t2", new JSONMaptron( "{ k: v}" ) );

        Debug.trace( structure, structure.findDataField( "t2" ), structure.findTextField( "__NAME__" ), structure.findTextField( "sss" ) );

        structure.resize( 4 );
        structure.setDataField( 3, "t3", 3 );
        Debug.trace( structure );

        //structure.setDataOffset( 2 );
        //structure.setTextOffset( 3 );
        Debug.trace( structure, structure.size(), structure.capacity() );







        Method[] methods = Raccoon.class.getMethods();
        GenericArgumentRequest request = new GenericArgumentRequest( Raccoon.class.getName(), methods[0].getParameterTypes() );

        Debug.trace( request, request.getAddressPath(), request.getInterceptedPath(), request.getInterceptorName(), request.getSegments() );


        Raccoon raccoon = new RedRaccoon();
        Debug.trace( raccoon.scratch( "you", 166 ) );
    }

    protected void testIfacInspector() throws Exception {
        BytecodeIfacCompiler inspector = new BytecodeIfacCompiler( ClassPool.getDefault() );

        Debug.trace( inspector.inspect( Raccoon.class, false ) );
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