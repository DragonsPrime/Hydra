package com.pinecone.hydra.umct;

import java.io.IOException;

import com.pinecone.framework.system.prototype.Pinenut;

public class DenialServiceException extends ServiceException implements Pinenut {
    public DenialServiceException() {
        super();
    }

    public DenialServiceException( String message ) {
        super(message);
    }

    public DenialServiceException( String message, Throwable cause ) {
        super(message, cause);
    }

    public DenialServiceException( Throwable cause ) {
        super(cause);
    }
}
