package com.pinecone.hydra.umb;

import com.pinecone.hydra.umct.ServiceException;

public class UMBClientException extends ServiceException {
    public UMBClientException() {
        super();
    }

    public UMBClientException( String message ) {
        super(message);
    }

    public UMBClientException( String message, Throwable cause ) {
        super(message, cause);
    }

    public UMBClientException( Throwable cause ) {
        super(cause);
    }
}
