package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCMessage;

import java.io.IOException;

public final class InternalErrors {
    public static void sendInternalError( ChannelControlBlock channel, Status errorCode ) throws IOException {
        UMCMessage errorMsg = new UlfInformMessage( (Object) null );
        errorMsg.getHead().setStatus( errorCode );
        channel.sendMsg( errorMsg, true );
    }

    public static void sendDefaultInternalError( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.InternalError );
    }

    public static void sendNotImplemented( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.NotImplemented );
    }

    public static void sendBadGateway( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.BadGateway );
    }

    public static void sendUnavailable( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.Unavailable );
    }

    public static void sendGatewayTimeout( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.GatewayTimeout );
    }

    public static void sendVersionNotSupported( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.VersionNotSupported );
    }

    public static void sendTooManyConnections( ChannelControlBlock channel ) throws IOException {
        InternalErrors.sendInternalError( channel, Status.TooManyConnections );
    }
}
