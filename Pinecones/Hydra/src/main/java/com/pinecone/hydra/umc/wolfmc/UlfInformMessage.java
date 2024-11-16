package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ArchInformMessage;
import com.pinecone.hydra.umc.msg.UMCHead;

import java.util.Map;

public class UlfInformMessage extends ArchInformMessage {
    public UlfInformMessage( UMCHead head ) {
        super(head);
    }

    public UlfInformMessage( Map<String,Object > joExHead, long controlBits ) {
        super( joExHead, controlBits );
    }

    public UlfInformMessage( Object protoExHead , long controlBits ) {
        super( protoExHead, controlBits );
    }

    public UlfInformMessage( Map<String,Object > joExHead ) {
        super( joExHead );
    }

    public UlfInformMessage( Object protoExHead ) {
        super( protoExHead );
    }
}
