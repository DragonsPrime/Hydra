package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ArchUMCReceiver;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.PostMessage;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;

import java.io.IOException;

public class UlfMCReceiver extends ArchUMCReceiver {
    public UlfMCReceiver( Medium messageSource ) {
        super( messageSource );
    }

    public UMCMessage readPostMsg( boolean bAllBytes ) throws IOException {
        UMCHead head = this.readPostHead();

        PostMessage message;
        if( bAllBytes ) {
            message = new UlfBytesPostMessage( head );
        }
        else {
            message = new UlfStreamPostMessage( head );
        }
        this.onlyReadPostBody( message, bAllBytes );
        return message;
    }

    @Override
    public UMCMessage readPostMsg() throws IOException {
        return this.readPostMsg( false );
    }

    @Override
    public UMCMessage readPostMsgBytes() throws IOException {
        return this.readPostMsg( true );
    }

    @Override
    public UMCMessage readMsg() throws IOException {
        return this.readMsg( false, UlfMessageStereotypes.Default );
    }

    @Override
    public UMCMessage readMsgBytes() throws IOException {
        return this.readMsg( true, UlfMessageStereotypes.Default );
    }
}
