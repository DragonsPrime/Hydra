package com.pinecone.hydra.umc.msg;

import java.util.Map;

public abstract class ArchBytesTransferMessage extends ArchUMCMessage implements TransferMessage {
    protected byte[]         msBytesBody   = null ;

    public ArchBytesTransferMessage( UMCHead head ) {
        super( head );
    }

    public ArchBytesTransferMessage( UMCHead head, byte[] sBytesBody   ) {
        this( head );
        this.setBody( sBytesBody );
    }

    public ArchBytesTransferMessage( UMCHead head, String szStringBody ) {
        this( head, szStringBody.getBytes() );
    }

    public ArchBytesTransferMessage( Map<String,Object > joExHead, byte[] sBytesBody, long controlBits ) {
        super( joExHead, UMCMethod.TRANSFER, controlBits );
        this.setBody( sBytesBody );
    }

    public ArchBytesTransferMessage( Map<String,Object > joExHead, String szStringBody, long controlBits ) {
        this( joExHead, szStringBody.getBytes(), controlBits );
    }

    public ArchBytesTransferMessage( Map<String,Object > joExHead, byte[] sBytesBody ) {
        this( joExHead, sBytesBody, 0 );
    }

    public ArchBytesTransferMessage( Map<String,Object > joExHead, String szStringBody ) {
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
