package com.pinecone.hydra.umc.wolfmc;

import java.io.InputStream;
import java.util.Map;

import com.pinecone.hydra.umc.msg.ArchStreamTransferMessage;
import com.pinecone.hydra.umc.msg.UMCHead;

public class UlfStreamTransferMessage extends ArchStreamTransferMessage {
    public UlfStreamTransferMessage( UMCHead head ) {
        super( head );
    }

    public UlfStreamTransferMessage( UMCHead head, InputStream inStream ) {
        super( head, inStream );
    }

    public UlfStreamTransferMessage( Map<String,Object > joExHead, InputStream inStream, long controlBits ) {
        super( joExHead, inStream, controlBits );
    }

    public UlfStreamTransferMessage( Map<String,Object > joExHead, InputStream inStream ) {
        super( joExHead, inStream, 0 );
    }
}