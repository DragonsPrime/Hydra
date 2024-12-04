package com.pinecone.hydra.umct;
import com.pinecone.hydra.umc.msg.*;

import io.netty.channel.ChannelHandlerContext;

public class UlfConnection extends ArchUMCConnection {
    protected Object[]  mArguments;

    UlfConnection( Medium medium, UMCMessage message, UMCTransmit transmit, UMCReceiver receiver ) {
        super( medium, message, transmit, receiver );
    }


    public UlfConnection( Medium medium, UMCMessage message, UMCTransmit transmit, UMCReceiver receiver, Object[] args ) {
        this( medium, message, transmit, receiver );
        this.mArguments = args;
    }

    public Object[] getExArguments() {
        return this.mArguments;
    }

    @Override
    public void release() {
        super.release();
    }

}
