package com.mc;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import io.netty.channel.ChannelHandlerContext;
import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfBytesTransferMessage;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;

import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.sauron.radium.messagron.Messagron;

class Jesus extends JesusChrist {
    public Jesus( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Jesus( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
        this.testClient();
    }

    public void testClient() throws Exception {
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
        JSONObject jsonObject = new JSONMaptron(
                "{Messagelet:'ServiceCenter', 'do': 'queryHeistConfTPL', 'heist': 'NeteaseMusic', 'instance': 'RavageAlbums'}"
        );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '1'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 1 fuck me" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '2'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 2 fuck me" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '3'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 3 fuck me" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '4'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 4 fuck me" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '5'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 5 fuck me" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '6'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 6 fuck he" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '7'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 7 fuck she" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '8'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 8 fuck it" ) ) );
//        Debug.trace( wolf.sendSyncMsg( new UlfBytesTransferMessage( new JSONMaptron( "{Messagelet:'ServiceCenter', 'do': '9'}" ), "test 12345678 Messagers.Messagers.WolfMCKingpin 9 fuck those" ) ) );



//        JSONObject jo = new JSONMaptron( "{'do': 'Morning' }" );
//        try ( ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); ObjectOutputStream objectStream = new ObjectOutputStream(byteStream) ) {
//            objectStream.writeObject( jo );
//            byte[] bytes = byteStream.toByteArray();
//            Debug.trace( wolf.sendSyncMsg( new UlfInformMessage( bytes, 0xAEF2048 ) ) );
//        }






        Debug.trace( wolf.sendSyncMsg( new UlfInformMessage( jsonObject ) ).getHead().getExtraHead() );
        Debug.trace( wolf.sendSyncMsg( new UlfInformMessage( jsonObject ) ) );
        //wolf.sendAsynMsg( new UlfInformMessage( jsonObject ) );

        wolf.sendAsynMsg( new UlfInformMessage(jsonObject), new UlfAsyncMsgHandleAdapter() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                Debug.trace( "Ajax" ,msg );
                Debug.trace( msg.getHead() );
            }
        });

        //wolf.sendAsynMsg( new UlfMCMessage( jsonObject ) );
        //wolf.sendAsynMsg( new UlfMCMessage( jsonObject ) );

        wolf.sendAsynMsg( new UlfInformMessage(jsonObject), new UlfAsyncMsgHandleAdapter() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                Debug.trace( "fuck javascript" ,msg );
            }
        });
        this.getTaskManager().add( wolf );


        this.getTaskManager().syncWaitingTerminated();
    }
}


public class TestMCClient {
    public static void main( String[] args ) throws Exception {
        //String[] as = args;
        String[] as = new String[]{ "TestWolfMCClient=true" };
        Pinecone.init( (Object...cfg )->{
            Jesus jesus = (Jesus) Pinecone.sys().getTaskManager().add( new Jesus( as, Pinecone.sys() ) );
            jesus.vitalize();
            return 0;
        }, (Object[]) as );
    }
}