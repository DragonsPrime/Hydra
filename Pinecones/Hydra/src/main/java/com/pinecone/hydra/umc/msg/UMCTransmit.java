package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.util.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public interface UMCTransmit extends UMCProtocol{
    void sendInformMsg( JSONObject msg ) throws IOException;

    void sendInformMsg( JSONObject msg, Status status ) throws IOException;

    void sendTransferMsg( JSONObject msg, byte[] bytes ) throws IOException;

    void sendTransferMsg( JSONObject msg, byte[] bytes, Status status ) throws IOException;

    default void sendTransferMsg( JSONObject msg, String sz ) throws IOException {
        this.sendTransferMsg( msg, sz.getBytes() );
    }

    void sendTransferMsg( JSONObject msg, InputStream is ) throws IOException;

    void sendMsg( UMCMessage msg, boolean bNoneBuffered ) throws IOException;

    default void sendMsg( UMCMessage msg ) throws IOException {
        this.sendMsg( msg, false );
    }
}
