package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ArchPutMessage;
import com.pinecone.hydra.umc.msg.UMCHead;

import java.util.Map;

public class UlfPutMessage extends ArchPutMessage {
    public UlfPutMessage( UMCHead head ) {
        super(head);
    }

    public UlfPutMessage( Map<String,Object > joExHead ) {
        super( joExHead );
    }

    public UlfPutMessage( Object protoExHead ) {
        super( protoExHead );
    }
}
