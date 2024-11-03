package com.mc;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import io.netty.channel.ChannelHandlerContext;
import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.messagram.WolfMCExpress;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;

import com.pinecone.hydra.umc.wolfmc.UlfMCMessage;
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
                "      \"Engine\": \"com.pinecone.hydra.messagram.WolfMCExpress\"\n" +
                "    }\n" +
                "  }\n" +
                "}" ) );

        WolfMCClient wolf = new WolfMCClient( "", this, this.getMiddlewareManager().getMiddlewareConfig().queryJSONObject( "Messagers.Messagers.WolfMCKingpin" ) );
        wolf.apply( new WolfMCExpress( servtron ) ).execute();
        JSONObject jsonObject = new JSONMaptron(
                "{Messagelet:'ServiceCenter', 'do': 'queryHeistConfTPL', 'heist': 'NeteaseMusic', 'instance': 'RavageAlbums'}"
        );
        //Debug.trace( wolf.sendSyncMsg( new UlfMCMessage( jsonObject, "fuck me" ) ) );

        Debug.trace( wolf.sendSyncMsg( new UlfMCMessage( jsonObject, new byte[]{ 123 } ) ) );

        Debug.trace( wolf.sendSyncMsg( new UlfMCMessage( jsonObject ) ).getHead().getExtraHead() );
        Debug.trace( wolf.sendSyncMsg( new UlfMCMessage( jsonObject ) ) );
        //wolf.sendAsynMsg( new UlfMCMessage( jsonObject ) );

        wolf.sendAsynMsg( new UlfMCMessage(jsonObject), new UlfAsyncMsgHandleAdapter() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                Debug.trace( "Ajax" ,msg );
                Debug.trace( msg.getHead() );
            }
        });

        //wolf.sendAsynMsg( new UlfMCMessage( jsonObject ) );
        //wolf.sendAsynMsg( new UlfMCMessage( jsonObject ) );

        wolf.sendAsynMsg( new UlfMCMessage(jsonObject), new UlfAsyncMsgHandleAdapter() {
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