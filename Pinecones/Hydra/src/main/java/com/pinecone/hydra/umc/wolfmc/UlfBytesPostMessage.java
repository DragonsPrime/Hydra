package com.pinecone.hydra.umc.wolfmc;

import java.util.Map;

import com.pinecone.hydra.umc.msg.ArchBytesPostMessage;
import com.pinecone.hydra.umc.msg.UMCHead;

public class UlfBytesPostMessage extends ArchBytesPostMessage {
    public UlfBytesPostMessage( UMCHead head ) {
        super( head );
    }

    public UlfBytesPostMessage( UMCHead head, byte[] sBytesBody   ) {
        super( head, sBytesBody );
    }

    public UlfBytesPostMessage( UMCHead head, String szStringBody ) {
        this( head, szStringBody.getBytes() );
    }

    public UlfBytesPostMessage( Map<String,Object > joExHead, byte[] sBytesBody ) {
        super( joExHead, sBytesBody );
    }

    public UlfBytesPostMessage( Map<String,Object > joExHead, String szStringBody ) {
        this( joExHead, szStringBody.getBytes() );
    }
}
