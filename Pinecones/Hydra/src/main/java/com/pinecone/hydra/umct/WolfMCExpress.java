package com.pinecone.hydra.umct;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import io.netty.channel.ChannelHandlerContext;
import com.pinecone.framework.system.ProvokeHandleException;

import java.io.IOException;

/**
 *  Pinecone Ursus For Java Hydra Ulfar, Wolf Express
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 */
public class WolfMCExpress extends ArchMsgExpress implements UlfAsyncMsgHandleAdapter {
    protected MessageletMsgDeliver mMessageletMsgDeliver;

    public WolfMCExpress( String name, ArchMessagram messagram ) {
        super( name, messagram );
        this.mMessageletMsgDeliver = (MessageletMsgDeliver) this.recruit( "Messagelet" );
    }

    public WolfMCExpress( ArchMessagram messagram ) {
        this( null, messagram );
    }

    protected MessageDeliver spawn( String szName ) { // TODO
        if( szName.equals( "Messagelet" ) ) {
            return new MessageletMsgDeliver( this );
        }
        return null;
    }

    @Override
    public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws IOException {
        UlfConnection msgPackage = new UlfConnection(  medium, block, msg, ctx );
        this.mMessageletMsgDeliver.toDispatch( msgPackage );
        msgPackage.release();
    }

    @Override
    public void onError( ChannelHandlerContext ctx, Throwable cause ) {
        if( cause instanceof Exception ) {
            this.warnSimple( cause.getMessage() );
        }
        else {
            throw new ProvokeHandleException( cause );
        }
    }
}
