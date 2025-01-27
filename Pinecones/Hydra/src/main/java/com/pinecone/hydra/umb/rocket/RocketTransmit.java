package com.pinecone.hydra.umb.rocket;

import java.io.IOException;
import java.io.InputStream;

import com.pinecone.hydra.umb.broadcast.ArchUnidirectionalMCProtocol;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCTransmit;

public class RocketTransmit extends ArchUnidirectionalMCProtocol implements UMCTransmit {

    public RocketTransmit( Medium messageSource ) {
        super( messageSource );
    }

    @Override
    public void sendInformMsg( Object msg ) throws IOException {

    }

    @Override
    public void sendInformMsg( Object msg, Status status ) throws IOException {

    }

    @Override
    public void sendTransferMsg( Object msg, byte[] bytes ) throws IOException {

    }

    @Override
    public void sendTransferMsg( Object msg, byte[] bytes, Status status ) throws IOException {

    }

    @Override
    public void sendTransferMsg( Object msg, InputStream is ) throws IOException {

    }

    @Override
    public void sendMsg( UMCMessage msg, boolean bNoneBuffered ) throws IOException {

    }
}
