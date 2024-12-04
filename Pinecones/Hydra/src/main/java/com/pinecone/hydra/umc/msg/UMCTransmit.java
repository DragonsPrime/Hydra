package com.pinecone.hydra.umc.msg;

import java.io.IOException;
import java.io.InputStream;

public interface UMCTransmit extends UMCProtocol {

    void sendInformMsg( Object msg ) throws IOException;

    void sendInformMsg( Object msg, Status status ) throws IOException;

    void sendTransferMsg( Object msg, byte[] bytes ) throws IOException;

    void sendTransferMsg( Object msg, byte[] bytes, Status status ) throws IOException;

    default void sendTransferMsg( Object msg, String sz ) throws IOException {
        this.sendTransferMsg( msg, sz.getBytes() );
    }

    void sendTransferMsg( Object msg, InputStream is ) throws IOException;


    void sendMsg( UMCMessage msg, boolean bNoneBuffered ) throws IOException;

    default void sendMsg( UMCMessage msg ) throws IOException {
        this.sendMsg( msg, false );
    }
}
