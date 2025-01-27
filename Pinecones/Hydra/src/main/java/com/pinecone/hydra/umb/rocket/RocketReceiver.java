package com.pinecone.hydra.umb.rocket;

import java.io.IOException;

import com.pinecone.hydra.umb.broadcast.ArchUnidirectionalMCProtocol;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;

public class RocketReceiver extends ArchUnidirectionalMCProtocol implements UMCReceiver {

    public RocketReceiver( Medium messageSource ) {
        super( messageSource );
    }

    @Override
    public Object readInformMsg() throws IOException {
        return null;
    }

    @Override
    public UMCMessage readTransferMsg() throws IOException {
        return null;
    }

    @Override
    public UMCMessage readTransferMsgBytes() throws IOException {
        return null;
    }

    @Override
    public UMCMessage readMsg() throws IOException {
        return null;
    }

    @Override
    public UMCMessage readMsgBytes() throws IOException {
        return null;
    }
}
