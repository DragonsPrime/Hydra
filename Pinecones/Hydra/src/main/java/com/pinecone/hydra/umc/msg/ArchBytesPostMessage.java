package com.pinecone.hydra.umc.msg;

import java.util.Map;

public abstract class ArchBytesPostMessage extends ArchUMCMessage implements PostMessage {
    protected byte[]         msBytesBody   = null ;

    public ArchBytesPostMessage( UMCHead head ) {
        super( head );
    }

    public ArchBytesPostMessage( UMCHead head, byte[] sBytesBody   ) {
        this( head );
        this.setBody( sBytesBody );
    }

    public ArchBytesPostMessage( UMCHead head, String szStringBody ) {
        this( head, szStringBody.getBytes() );
    }

    public ArchBytesPostMessage( Map<String,Object > joExHead, byte[] sBytesBody, long controlBits ) {
        super( joExHead, UMCMethod.POST, controlBits );
        this.setBody( sBytesBody );
    }

    public ArchBytesPostMessage( Map<String,Object > joExHead, String szStringBody, long controlBits ) {
        this( joExHead, szStringBody.getBytes(), controlBits );
    }

    public ArchBytesPostMessage( Map<String,Object > joExHead, byte[] sBytesBody ) {
        this( joExHead, sBytesBody, 0 );
    }

    public ArchBytesPostMessage( Map<String,Object > joExHead, String szStringBody ) {
        this( joExHead, szStringBody, 0 );
    }



    void setBody( byte[] sBytesBody ) {
        this.msBytesBody = sBytesBody;
        this.mHead.setBodyLength( this.msBytesBody.length );
    }

    public byte[]      getBody() {
        return this.msBytesBody;
    }

    @Override
    public void        release() {
        super.release();
        this.msBytesBody  = null;
    }
}
