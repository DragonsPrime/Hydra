package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.ProvokeHandleException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class ArchUMCReceiver extends ArchUMCProtocol implements UMCReceiver {
    public ArchUMCReceiver( Medium messageSource ) {
        super( messageSource );
    }

    @Override
    public Object readPutMsg() throws IOException {
        UMCHead head = this.readMsgHead();
        if( head.method != UMCMethod.PUT ) {
            throw new IOException( "[UMCProtocol] Illegal protocol method." );
        }
        return head.getExtraHead();
    }

    protected UMCHead readPostHead() throws IOException {
        UMCHead head = this.readMsgHead();
        if( head.method != UMCMethod.POST ) {
            throw new IOException( "[UMCProtocol] Illegal protocol method." );
        }
        return head;
    }

    protected void onlyReadPostBody( PostMessage message, boolean bAllBytes ) throws IOException {
        if( bAllBytes ) {
            ( (ArchBytesPostMessage)message ).setBody( this.mInputStream.readAllBytes() );
        }
        else {
            ( (ArchStreamPostMessage)message ).setBody( this.mInputStream );
        }
    }

    public UMCMessage readMsg( boolean bAllBytes, MessageStereotypes stereotypes ) throws IOException {
        try{
            UMCHead head = this.readMsgHead();
            UMCMessage message;
            if( head.getMethod() == UMCMethod.POST ){
                if( bAllBytes ) {
                    message = (UMCMessage) stereotypes.postBytesType().getConstructor( UMCHead.class ).newInstance( head );
                }
                else {
                    message = (UMCMessage) stereotypes.postStreamType().getConstructor( UMCHead.class ).newInstance( head );
                }
                this.onlyReadPostBody( (PostMessage)message, bAllBytes );
            }
            else {
                if( head.getMethod() != UMCMethod.PUT ){
                    throw new IOException( " [UMCProtocol] Unrecognized protocol method." );
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
