package com.pinecone.hydra.umc.wolfmc;

import java.io.InputStream;
import java.util.Map;

import com.pinecone.hydra.umc.msg.ArchStreamPostMessage;
import com.pinecone.hydra.umc.msg.UMCHead;

public class UlfStreamPostMessage extends ArchStreamPostMessage {
    public UlfStreamPostMessage( UMCHead head ) {
        super( head );
    }

    public UlfStreamPostMessage( UMCHead head, InputStream inStream ) {
        super( head, inStream );
    }

    public UlfStreamPostMessage( Map<String,Object > joExHead, InputStream inStream ) {
        super( joExHead, inStream );
    }
}