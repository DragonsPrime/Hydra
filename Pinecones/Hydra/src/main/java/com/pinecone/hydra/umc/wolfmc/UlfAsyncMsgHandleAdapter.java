package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.AsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;

import io.netty.channel.ChannelHandlerContext;

import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.UMCTExpressHandler;

public interface UlfAsyncMsgHandleAdapter extends AsyncMsgHandleAdapter {
    default void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
        this.onSuccessfulMsgReceived( medium, block.getTransmit(), block.getReceiver(), msg, new Object[]{ block, rawMsg } );
    }

    default void onErrorMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
        this.onErrorMsgReceived( medium, block.getTransmit(), block.getReceiver(), msg, new Object[]{ block, rawMsg } );
    }

    default void onError( ChannelHandlerContext ctx, Throwable cause ) {
        this.onError( (Object) ctx, cause );
    }

    static UlfAsyncMsgHandleAdapter wrap( UMCTExpressHandler handler ) {
        return new UlfAsyncMsgHandleAdapter() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                handler.onSuccessfulMsgReceived( medium, block.getTransmit(), block.getReceiver(), msg, new Object[]{ block, rawMsg } );
            }

            @Override
            public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                handler.onSuccessfulMsgReceived( medium, transmit, receiver, msg, args );
            }

            @Override
            public void onErrorMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                handler.onErrorMsgReceived( medium, block.getTransmit(), block.getReceiver(), msg, new Object[]{ block, rawMsg } );
            }

            @Override
            public void onErrorMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                handler.onErrorMsgReceived( medium, transmit, receiver, msg, args );
            }

            @Override
            public void onError( ChannelHandlerContext ctx, Throwable cause ) {
                handler.onError( (Object) ctx, cause );
            }

            @Override
            public void onError( Object data, Throwable cause ) {
                handler.onError( data, cause );
            }
        };
    }

}