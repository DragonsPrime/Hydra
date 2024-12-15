package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;

import io.netty.channel.ChannelHandlerContext;

/**
 * UnsetUlfAsyncMsgHandleAdapter
 * Dummy UlfAsyncMsgHandleAdapter
 */
public final class UnsetUlfAsyncMsgHandleAdapter implements UlfAsyncMsgHandleAdapter {
    private MessageNode mMessageNode;

    public UnsetUlfAsyncMsgHandleAdapter( MessageNode node ) {
        this.mMessageNode = node;
    }

    @Override
    public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) {
        Debug.warn( "Warning, MsgHandleAdapter is unset.", block.getChannel().getChannelID(), msg );
    }

    @Override
    public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        Debug.warn( "Warning, MsgHandleAdapter is unset.", msg );
    }

    @Override
    public void onErrorMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        Debug.warn( "Warning, MsgHandleAdapter is unset.", msg );
    }

    @Override
    public void onErrorMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) {
        Debug.warn( "Warning, MsgHandleAdapter is unset.", block.getChannel().getChannelID(), msg );
    }

    @Override
    public void onError( ChannelHandlerContext ctx, Throwable cause ) {
        this.onError( (Object) ctx, cause );
    }

    @Override
    public void onError( Object data, Throwable cause ) {
        if( !( cause instanceof Exception ) ) {
            throw new ProvokeHandleException( cause );
        }
    }
}