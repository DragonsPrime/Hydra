package com.pinecone.hydra.umc.wolfmc;

import java.util.Map;

import com.pinecone.hydra.umc.msg.ArchBytesTransferMessage;
import com.pinecone.hydra.umc.msg.UMCHead;

public class UlfBytesTransferMessage extends ArchBytesTransferMessage {
    public UlfBytesTransferMessage( UMCHead head ) {
        super( head );
    }

    public UlfBytesTransferMessage( UMCHead head, byte[] sBytesBody   ) {
        super( head, sBytesBody );
    }

    public UlfBytesTransferMessage( UMCHead head, String szStringBody ) {
        this( head, szStringBody.getBytes() );
    }

    public UlfBytesTransferMessage( Map<String,Object > joExHead, byte[] sBytesBody, long controlBits ) {
        super( joExHead, sBytesBody, controlBits );
    }

    public UlfBytesTransferMessage( Map<String,Object > joExHead, String szStringBody, long controlBits ) {
        this( joExHead, szStringBody.getBytes(), controlBits );
    }

    public UlfBytesTransferMessage( Map<String,Object > joExHead, byte[] sBytesBody ) {
        this( joExHead, sBytesBody, 0 );
    }

    public UlfBytesTransferMessage( Map<String,Object > joExHead, String szStringBody ) {
        this( joExHead, szStringBody.getBytes(), 0 );
    }
}
