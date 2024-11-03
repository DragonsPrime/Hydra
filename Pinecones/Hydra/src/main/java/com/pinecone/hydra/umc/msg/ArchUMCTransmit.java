package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.util.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public abstract class ArchUMCTransmit extends ArchUMCProtocol implements UMCTransmit {
    public ArchUMCTransmit( Medium messageSource ) {
        super( messageSource );
    }

    @Override
    public void sendPutMsg( JSONObject msg, Status status ) throws IOException {
        this.mHead.applyExHead( msg );
        this.mHead.setStatus( status );
        this.mHead.setMethod( UMCMethod.PUT );
        this.sendMsgHead( this.mHead );
    }

    @Override
    public void sendPutMsg( JSONObject msg ) throws IOException {
        this.sendPutMsg( msg, Status.OK );
    }

    public void sendPostMsgHead( JSONObject msg ) throws IOException {
        this.sendPostMsgHead( msg, false );
    }

    public void sendPostMsgHead( JSONObject msg, boolean bFlush ) throws IOException {
        this.mHead.applyExHead( msg );
        this.mHead.setMethod( UMCMethod.POST );
        this.sendMsgHead( this.mHead, bFlush );
    }

    public void sendPostMsgContent( byte[] frame, int len ) throws IOException {
        this.mOutputStream.write( frame, 0, len );
    }


    protected void onlySendPostBody( byte[] bytes ) throws IOException {
        this.sendPostMsgContent( bytes, bytes.length );
        this.mOutputStream.flush();
    }

    @Override
    public void sendPostMsg( JSONObject msg, byte[] bytes, Status status ) throws IOException {
        this.mHead.setBodyLength( bytes.length );
        this.mHead.setStatus( status );
        this.sendPostMsgHead( msg, false );
        this.onlySendPostBody( bytes );
    }

    @Override
    public void sendPostMsg( JSONObject msg, byte[] bytes ) throws IOException {
        this.sendPostMsg( msg, bytes, Status.OK );
    }

    protected void onlySendPostBody( InputStream is, boolean bNoneBuffered ) throws IOException {
        //this.mnFrameSize = 2;
        byte[] buf;
        if( bNoneBuffered ) {
            buf = is.readAllBytes();
            this.sendPostMsgContent( buf, buf.length );
        }
        else {
            buf = new byte[ this.mnFrameSize ];
            while ( true ) {
                int n = is.available();

                if( n > this.mnFrameSize && is.read( buf ) > 0 ) {
                    this.sendPostMsgContent( buf, this.mnFrameSize );
                }
                else {
                    if( is.read( buf, 0, n ) > 0 ) {
                        this.sendPostMsgContent( buf, n );
                    }
                    break;
                }
            }
        }

        this.getMessageSource().getOutputStream().flush();
    }

    @Override
    public void sendPostMsg( JSONObject msg, InputStream is ) throws IOException {
        this.mHead.setBodyLength( is.available() );
        this.sendPostMsgHead( msg, false );
        this.onlySendPostBody( is, false );
    }


    @Override
    public void sendMsg( UMCMessage msg, boolean bNoneBuffered ) throws IOException {
        this.mHead = msg.getHead();
        this.mHead.setSignature( this.mszSignature );

        if( msg.getMethod() == UMCMethod.PUT ) {
            this.sendMsgHead( this.mHead );
        }
        else if( msg.getMethod() == UMCMethod.POST ) {
            this.sendMsgHead( this.mHead, false );
            Object body = msg.evincePostMessage().getBody();
            if( body instanceof byte[] ) {
                byte[] bytes = (byte[])body;
                this.onlySendPostBody( bytes );
            }
            else if( body instanceof InputStream ) {
                InputStream is = (InputStream)body;
                this.onlySendPostBody( is, bNoneBuffered );
            }
        }
    }


}
