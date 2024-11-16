package com.pinecone.hydra.umct;
import com.pinecone.hydra.umc.msg.*;

import io.netty.channel.ChannelHandlerContext;

public class UlfMsgPackage extends ArchMsgPackage {
    protected ChannelControlBlock    mUlfChannelControlBlock;
    protected ChannelHandlerContext  mChannelHandlerContext;

    UlfMsgPackage(Medium medium, UMCMessage message, UMCTransmit transmit, UMCReceiver receiver ) {
        super( medium, message, transmit, receiver );
    }


    public UlfMsgPackage(Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx ) {
        this( medium, msg, block.getTransmit(), block.getReceiver() );
        this.mUlfChannelControlBlock = block;
        this.mChannelHandlerContext  = ctx;
    }

    public ChannelControlBlock getUlfChannelControlBlock() {
        return this.mUlfChannelControlBlock;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return this.mChannelHandlerContext;
    }

    @Override
    public void release() {
        super.release();
        this.mUlfChannelControlBlock = null;
        this.mChannelHandlerContext  = null;
    }

}
