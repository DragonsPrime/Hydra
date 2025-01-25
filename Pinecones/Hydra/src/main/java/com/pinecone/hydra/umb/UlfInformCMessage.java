package com.pinecone.hydra.umb;

import com.pinecone.hydra.umc.msg.ArchInformCMessage;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.UMCCHead;
import com.pinecone.hydra.umc.msg.UMCHead;

import java.util.Map;

public class UlfInformCMessage extends ArchInformCMessage {
    public UlfInformCMessage( UMCCHead head ) {
        super( head );
    }

    public UlfInformCMessage( Map<String,Object > joExHead ) {
        super( joExHead );
    }

    public UlfInformCMessage( Object protoExHead ) {
        super( protoExHead );
    }
}
