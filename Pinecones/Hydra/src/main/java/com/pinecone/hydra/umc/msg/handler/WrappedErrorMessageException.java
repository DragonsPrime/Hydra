package com.pinecone.hydra.umc.msg.handler;

import com.pinecone.framework.system.PineRuntimeException;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;

public class WrappedErrorMessageException extends PineRuntimeException {
    protected Status status;

    protected UMCMessage message;

    public WrappedErrorMessageException( Status status ) {
        super();

        this.status = status;
    }

    public WrappedErrorMessageException( String message, Status status  ) {
        super( message );

        this.status = status;
    }

    public WrappedErrorMessageException( String message, Throwable cause, Status status ) {
        super( message, cause );

        this.status = status;
    }

    public WrappedErrorMessageException( Throwable cause, Status status ) {
        super(cause);

        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public UMCMessage getUMCMessage() {
        return this.message;
    }

    public void setUMCMessage( UMCMessage message ) {
        this.message = message;
    }

    public static WrappedErrorMessageException wrap( UMCHead head ) {
        Object what = head.getExHeaderVal( "What" );
        if( what instanceof String ) {
            return new WrappedErrorMessageException( (String) what, head.getStatus() );
        }

        return new WrappedErrorMessageException( head.getStatus() );
    }

    public static WrappedErrorMessageException wrap( UMCMessage message ) {
        WrappedErrorMessageException exception = WrappedErrorMessageException.wrap( message.getHead() );
        exception.setUMCMessage( message );
        return exception;
    }
}
