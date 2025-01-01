package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;

import io.netty.channel.ChannelHandlerContext;


public interface AsynMsgHandler extends Pinenut {
    default void onSuccessfulMsgReceived( UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg ) throws Exception {
        this.onSuccessfulMsgReceived( msg );
    }

    void onSuccessfulMsgReceived( UMCMessage msg ) throws Exception ;

    default void onErrorMsgReceived( UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg ) throws Exception {
        this.onErrorMsgReceived( msg );
    }

    void onErrorMsgReceived( UMCMessage msg ) throws Exception ;

    default void onError( Object data, Throwable cause ) {

    }

    static UlfAsyncMsgHandleAdapter wrap( AsynMsgHandler handler ) throws IOException {
        return new UlfAsyncMsgHandleAdapter() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                handler.onSuccessfulMsgReceived( block.getTransmit(), block.getReceiver(), msg );
            }

            @Override
            public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                handler.onSuccessfulMsgReceived( transmit, receiver, msg );
            }

            @Override
            public void onErrorMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                handler.onErrorMsgReceived( block.getTransmit(), block.getReceiver(), msg );
            }

            @Override
            public void onErrorMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                handler.onErrorMsgReceived( transmit, receiver, msg );
            }

            @Override
            public void onError( Object data, Throwable cause ) {
                handler.onError( data, cause );
            }
        };
    }
}
