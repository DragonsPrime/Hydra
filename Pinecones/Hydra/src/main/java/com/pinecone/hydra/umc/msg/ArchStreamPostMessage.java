package com.pinecone.hydra.umc.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ArchStreamPostMessage extends ArchUMCMessage implements PostMessage {
    protected InputStream    mIStreamBody  = null ;

    public ArchStreamPostMessage( UMCHead head ) {
        super( head );
        head.setMethod( UMCMethod.POST );
    }

    public ArchStreamPostMessage( UMCHead head, InputStream inStream ) {
        this( head );
        this.setBody( inStream );
    }

    public ArchStreamPostMessage( Map<String,Object > joExHead, InputStream inStream, long controlBits ) {
        super( joExHead, UMCMethod.POST, controlBits );
        this.setBody( inStream );
    }

    public ArchStreamPostMessage( Map<String,Object > joExHead, InputStream inStream ) {
        this( joExHead, inStream, 0 );
    }



    void setBody( InputStream inStream ) {
        this.mIStreamBody = inStream;
        try{
            this.mHead.setBodyLength( this.mIStreamBody.available() );
        }
        catch ( IOException e ) {
            this.mHead.setBodyLength( 0 );
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