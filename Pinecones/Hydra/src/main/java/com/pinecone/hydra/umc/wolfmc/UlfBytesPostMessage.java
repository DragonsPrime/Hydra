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

    public UlfBytesPostMessage( Map<String,Object > joExHead, byte[] sBytesBody, long controlBits ) {
        super( joExHead, sBytesBody, controlBits );
    }

    public UlfBytesPostMessage( Map<String,Object > joExHead, String szStringBody, long controlBits ) {
        this( joExHead, szStringBody.getBytes(), controlBits );
    }

    public UlfBytesPostMessage( Map<String,Object > joExHead, byte[] sBytesBody ) {
        this( joExHead, sBytesBody, 0 );
    }

    public UlfBytesPostMessage( Map<String,Object > joExHead, String szStringBody ) {
        this( joExHead, szStringBody.getBytes(), 0 );
    }
}
