package com.pinecone.hydra.umc.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public abstract class ArchUMCTransmit extends ArchUMCProtocol implements UMCTransmit {
    public ArchUMCTransmit( Medium messageSource ) {
        super( messageSource );
    }

    @SuppressWarnings( "unchecked" )
    protected void applyExHead( Object msg ) {
        if( msg instanceof Map ) {
            this.mTemplateHead.inface().applyExHead( (Map) msg );
        }
        else {
            this.mTemplateHead.inface().setExtraHead( msg );
        }
    }

    @Override
    public void sendInformMsg( Object msg, Status status ) throws IOException {
        this.applyExHead( msg );
        this.mTemplateHead.setStatus( status );
        this.mTemplateHead.inface().setMethod( UMCMethod.INFORM );
        this.sendMsgHead( this.mTemplateHead );
    }

    @Override
    public void sendInformMsg( Object msg ) throws IOException {
        this.sendInformMsg( msg, Status.OK );
    }

    public void sendTransferMsgHead( Object msg ) throws IOException {
        this.sendTransferMsgHead( msg, false );
    }


    public void sendTransferMsgHead( Object msg, boolean bFlush ) throws IOException {
        this.applyExHead( msg );
        this.mTemplateHead.inface().setMethod( UMCMethod.TRANSFER );
        this.sendMsgHead( this.mTemplateHead, bFlush );
    }

    public void sendTransferMsgContent( byte[] frame, int len ) throws IOException {
        this.mOutputStream.write( frame, 0, len );
    }


    protected void onlySendPostBody( byte[] bytes ) throws IOException {
        this.sendTransferMsgContent( bytes, bytes.length );
        this.mOutputStream.flush();
    }

    @Override
    public void sendTransferMsg( Object msg, byte[] bytes, Status status ) throws IOException {
        this.mTemplateHead.inface().setBodyLength( bytes.length );
        this.mTemplateHead.setStatus( status );
        this.sendTransferMsgHead( msg, false );
        this.onlySendPostBody( bytes );
    }

    @Override
    public void sendTransferMsg( Object msg, byte[] bytes ) throws IOException {
        this.sendTransferMsg( msg, bytes, Status.OK );
    }

    protected void onlySendPostBody( InputStream is, boolean bNoneBuffered ) throws IOException {
        //this.mnFrameSize = 2;
        byte[] buf;
        if( bNoneBuffered ) {
            buf = is.readAllBytes();
            this.sendTransferMsgContent( buf, buf.length );
        }
        else {
            buf = new byte[ this.mnFrameSize ];
            while ( true ) {
                int n = is.available();

                if( n > this.mnFrameSize && is.read( buf ) > 0 ) {
                    this.sendTransferMsgContent( buf, this.mnFrameSize );
                }
                else {
                    if( is.read( buf, 0, n ) > 0 ) {
                        this.sendTransferMsgContent( buf, n );
                    }
                    break;
                }
            }
        }

        this.getMessageSource().getOutputStream().flush();
    }

    @Override
    public void sendTransferMsg( Object msg, InputStream is ) throws IOException {
        this.mTemplateHead.inface().setBodyLength( is.available() );
        this.sendTransferMsgHead( msg, false );
        this.onlySendPostBody( is, false );
    }


    @Override
    public void sendMsg( UMCMessage msg, boolean bNoneBuffered ) throws IOException {
        msg.getHead().setIdentityId( this.getMessageSource().getMessageNode().getMessageNodeId() );
        this.mTemplateHead = msg.getHead();
        this.mTemplateHead.inface().setSignature( this.mszSignature );

        if( msg.getMethod() == UMCMethod.INFORM || msg.getMethod() == UMCMethod.UNDEFINED ) {
            this.sendMsgHead( this.mTemplateHead );
        }
        else if( msg.getMethod() == UMCMethod.TRANSFER ) {
            this.sendMsgHead( this.mTemplateHead, false );
            Object body = msg.evinceTransferMessage().getBody();
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
