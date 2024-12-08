package com.protobuf;


import java.util.List;
import java.util.Set;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.mc.JesusChrist;
import com.pinecone.Pinecone;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.pinecone.hydra.umc.wolfmc.server.WolfMCServer;
import com.pinecone.hydra.umct.MessageController;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.ProtoletMsgDeliver;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.protocol.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.protocol.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.protocol.compiler.MethodDigest;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.Options;
import com.sauron.radium.messagron.Messagron;

import javassist.ClassPool;

class Jeff extends JesusChrist {
    public Jeff( String[] args, CascadeSystem parent ) {
        this(args, null, parent);
    }

    public Jeff( String[] args, String szName, CascadeSystem parent ) {
        super(args, szName, parent);
    }

    @Override
    public void vitalize () throws Exception {
        this.testProtoRPCServer();

        this.testProtoRPCClient();
    }

    private void testProtoRPCServer() throws Exception {
        Messagron messagron = new Messagron( "", this, new JSONMaptron() );
        WolfMCServer wolf = new WolfMCServer( "", this, new JSONMaptron("{host: \"0.0.0.0\",\n" +
                "port: 5777, SocketTimeout: 800, KeepAliveTimeout: 3600, MaximumConnections: 1e6}") );


        UMCTExpress express = new WolfMCExpress( null, this );


        BytecodeIfacCompiler inspector = new BytecodeIfacCompiler( ClassPool.getDefault() );
        List<MethodDigest> digests = inspector.compile( Raccoon.class, false ).getMethodDigests();
        MethodDigest digest = digests.get( 0 );
        DynamicMethodPrototype prototype = (DynamicMethodPrototype) digest;


        MessageDeliver deliver = new ProtoletMsgDeliver( express, inspector.getCompilerEncoder() );
        deliver.registerController("com.protobuf.Raccoon.scratch", new MessageController() {
            @Override
            public String getAddressMapping() {
                return null;
            }

            @Override
            public Object invoke( Object... args ) throws Exception {
                Debug.purplef( args );

                return "miaomiao";
            }

            @Override
            public List<String> getArgumentsKey() {
                return null;
            }

            @Override
            public Object getReturnDescriptor() {
                return prototype.getReturnDescriptor();
            }

            @Override
            public Object getArgumentsDescriptor() {
                return prototype.getArgumentsDescriptor();
            }
        });


        express.register( deliver );

        wolf.apply( express );

        wolf.execute();

        this.getTaskManager().add( wolf );
        //this.getTaskManager().syncWaitingTerminated();
    }

    private void testProtoRPCClient() throws Exception {
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


        BytecodeIfacCompiler inspector = new BytecodeIfacCompiler( ClassPool.getDefault() );
        List<MethodDigest> digests = inspector.compile( Raccoon.class, false ).getMethodDigests();
        MethodDigest digest = digests.get( 0 );
        DynamicMethodPrototype prototype = (DynamicMethodPrototype) digest;

        GenericFieldProtobufEncoder encoder = new GenericFieldProtobufEncoder();
        Options options = new Options();

        Descriptors.Descriptor descriptor = prototype.getArgumentsDescriptor();
        Debug.trace( descriptor.getFields() );

        FieldEntity[] types = prototype.getArgumentTemplate().getSegments();
        types[1].setValue("fuck you");
        types[2].setValue(2024);
        DynamicMessage message = encoder.encode( descriptor, types, Set.of(), options );

        Debug.sleep( 500 );
        UMCMessage retMsg = wolf.sendSyncMsg(new UlfInformMessage(message.toByteArray()));
        if(retMsg instanceof UlfInformMessage) {
            Descriptors.Descriptor retDes = prototype.getReturnDescriptor();
            DynamicMessage rm = DynamicMessage.parseFrom( retDes, (byte[])retMsg.getHead().getExtraHead() );
            GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
            Object r = decoder.decode( prototype.getReturnType(), retDes, rm, Set.of(), options );
            Debug.trace(r);

        }
        this.getTaskManager().add( wolf );
        this.getTaskManager().syncWaitingTerminated();
    }
}

public class TestRPCSystem {
    public static void main( String[] args ) throws Exception {
        //String[] as = args;
        String[] as = new String[]{ "TestWolfMCClient=true" };
        Pinecone.init( (Object...cfg )->{
            Jeff jeff = (Jeff) Pinecone.sys().getTaskManager().add( new Jeff( as, Pinecone.sys() ) );
            jeff.vitalize();
            return 0;
        }, (Object[]) as );
    }
}
