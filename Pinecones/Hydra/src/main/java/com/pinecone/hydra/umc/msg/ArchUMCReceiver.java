package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.ProvokeHandleException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public abstract class ArchUMCReceiver extends ArchUMCProtocol implements UMCReceiver {
    public ArchUMCReceiver( Medium messageSource ) {
        super( messageSource );
    }

    @Override
    public Object readInformMsg() throws IOException {
        UMCHead head = this.readMsgHead();
        if( head.getMethod() != UMCMethod.INFORM ) {
            throw new IOException( "[UMCProtocol] Illegal protocol method." );
        }
        return head.getExtraHead();
    }

    protected UMCHead readTransferHead() throws IOException {
        UMCHead head = this.readMsgHead();
        if( head.getMethod() != UMCMethod.TRANSFER ) {
            throw new IOException( "[UMCProtocol] Illegal protocol method." );
        }
        return head;
    }

    protected void onlyReadTransferBody( TransferMessage message, boolean bAllBytes ) throws IOException {
        if( bAllBytes ) {
            ( (ArchBytesTransferMessage)message ).setBody( this.mInputStream.readAllBytes() );
        }
        else {
            ( (ArchStreamTransferMessage)message ).setBody( this.mInputStream );
        }
    }

    public UMCMessage readMsg( boolean bAllBytes, MessageStereotypes stereotypes ) throws IOException {
        try{
            UMCHead head = this.readMsgHead();
            UMCMessage message;
            if( head.getMethod() == UMCMethod.TRANSFER ){
                if( bAllBytes ) {
                    message = (UMCMessage) stereotypes.postBytesType().getConstructor( UMCHead.class ).newInstance( head );
                }
                else {
                    message = (UMCMessage) stereotypes.postStreamType().getConstructor( UMCHead.class ).newInstance( head );
                }
                this.onlyReadTransferBody( (TransferMessage)message, bAllBytes );
            }
            else {
                if( head.getMethod() != UMCMethod.INFORM ){
                    if ( !( head.getMethod() == UMCMethod.UNDEFINED && head.getExtraEncode() == ExtraEncode.Iussum ) ) {
                        throw new IOException( " [UMCProtocol] Unrecognized protocol method." );
                    }
                }
                message = (UMCMessage) stereotypes.putType().getConstructor( UMCHead.class ).newInstance( head );
            }

            return message;
        }
        catch ( NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e ) {
            throw new ProvokeHandleException( e );
        }
    }
}
