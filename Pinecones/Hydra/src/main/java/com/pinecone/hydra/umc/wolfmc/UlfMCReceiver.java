package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ArchUMCReceiver;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.TransferMessage;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;

import java.io.IOException;

public class UlfMCReceiver extends ArchUMCReceiver {
    public UlfMCReceiver( Medium messageSource ) {
        super( messageSource );
    }

    public UMCMessage readTransferMsg( boolean bAllBytes ) throws IOException {
        UMCHead head = this.readTransferHead();

        TransferMessage message;
        if( bAllBytes ) {
            message = new UlfBytesTransferMessage( head );
        }
        else {
            message = new UlfStreamTransferMessage( head );
        }
        this.onlyReadTransferBody( message, bAllBytes );
        return message;
    }

    @Override
    public UMCMessage readTransferMsg() throws IOException {
        return this.readTransferMsg( false );
    }

    @Override
    public UMCMessage readTransferMsgBytes() throws IOException {
        return this.readTransferMsg( true );
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
