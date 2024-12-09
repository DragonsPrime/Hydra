package com.protobuf;


import java.util.List;

import com.mc.JesusChrist;
import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.pinecone.hydra.umc.wolfmc.server.WolfMCServer;
import com.pinecone.hydra.umct.MessageHandler;
import com.pinecone.hydra.umct.appoint.WolfAppointClient;
import com.pinecone.hydra.umct.appoint.WolfAppointServer;
import com.pinecone.hydra.umct.protocol.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.protocol.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.protocol.compiler.MethodDigest;
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

        WolfMCServer wolf1 = new WolfMCServer( "", this, new JSONMaptron("{host: \"0.0.0.0\",\n" +
                "port: 5777, SocketTimeout: 800, KeepAliveTimeout: 3600, MaximumConnections: 1e6}") );
        WolfAppointServer wolf = new WolfAppointServer( wolf1 );


//        BytecodeIfacCompiler inspector = new BytecodeIfacCompiler( ClassPool.getDefault() );
//        List<MethodDigest> digests = inspector.compile( Raccoon.class, false ).getMethodDigests();
//        MethodDigest digest = digests.get( 0 );
//        DynamicMethodPrototype prototype = (DynamicMethodPrototype) digest;
//
//
//        wolf.getDefaultDeliver().registerController("com.protobuf.Raccoon.scratch", new MessageHandler() {
//            @Override
//            public String getAddressMapping() {
//                return null;
//            }
//
//            @Override
//            public Object invoke( Object... args ) throws Exception {
//                Debug.purplef( args );
//
//                return "miaomiao";
//            }
//
//            @Override
//            public List<String> getArgumentsKey() {
//                return null;
//            }
//
//            @Override
//            public Object getReturnDescriptor() {
//                return prototype.getReturnDescriptor();
//            }
//
//            @Override
//            public Object getArgumentsDescriptor() {
//                return prototype.getArgumentsDescriptor();
//            }
//        });


        RaccoonKing raccoonKing = new RaccoonKing();
        wolf.registerInstance( raccoonKing, Raccoon.class );

        wolf.execute();

        this.getTaskManager().add( wolf );
        //this.getTaskManager().syncWaitingTerminated();
    }

    private void testProtoRPCClient() throws Exception {
        WolfAppointClient wolf = new WolfAppointClient( new WolfMCClient( "", this, this.getMiddlewareManager().getMiddlewareConfig().queryJSONObject( "Messagers.Messagers.WolfMCKingpin" ) ) );
        wolf.execute();

        wolf.compile( Raccoon.class, false );
        DynamicMethodPrototype digest = (DynamicMethodPrototype)wolf.queryMethodDigest( "com.protobuf.Raccoon.scratch" );

        Debug.sleep( 200 );
//        wolf.invokeInformAsyn(digest, new Object[]{"fuck you", 2024}, new AsynReturnHandler() {
//            @Override
//            public void onSuccessfulReturn( Object ret ) throws Exception {
//                Debug.greenf( ret );
//            }
//
//            @Override
//            public void onErrorMsgReceived( UMCMessage msg ) throws Exception {
//
//            }
//        });

        Debug.greenf( wolf.invokeInform(digest, "fuck you", 2024 ) );

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
