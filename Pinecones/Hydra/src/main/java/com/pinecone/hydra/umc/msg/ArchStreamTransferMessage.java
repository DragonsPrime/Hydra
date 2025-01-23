package com.pinecone.hydra.umc.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ArchStreamTransferMessage extends ArchUMCMessage implements TransferMessage {
    protected InputStream    mIStreamBody  = null ;

    public ArchStreamTransferMessage( UMCHead head ) {
        super( head );
        head.inface().setMethod( UMCMethod.TRANSFER );
    }

    public ArchStreamTransferMessage( UMCHead head, InputStream inStream ) {
        this( head );
        this.setBody( inStream );
    }

    public ArchStreamTransferMessage( Map<String,Object > joExHead, InputStream inStream, long controlBits ) {
        super( joExHead, UMCMethod.TRANSFER, controlBits );
        this.setBody( inStream );
    }

    public ArchStreamTransferMessage( Map<String,Object > joExHead, InputStream inStream ) {
        this( joExHead, inStream, 0 );
    }



    public ArchStreamTransferMessage( Object exHead, ExtraEncode encode, InputStream inStream, long controlBits ) {
        super( exHead, encode, UMCMethod.TRANSFER, controlBits );
        this.setBody( inStream );
    }

    public ArchStreamTransferMessage( Object exHead, InputStream inStream ) {
        this( exHead, ExtraEncode.Prototype, inStream, 0 );
    }




    void setBody( InputStream inStream ) {
        this.mIStreamBody = inStream;
        try{
            this.mHead.inface().setBodyLength( this.mIStreamBody.available() );
        }
        catch ( IOException e ) {
            this.mHead.inface().setBodyLength( 0 );
        }
    }

    @Override
    public InputStream getBody() {
        return this.mIStreamBody;
    }

    @Override
    public void        release() {
        super.release();
        this.mIStreamBody  = null;
    }
}