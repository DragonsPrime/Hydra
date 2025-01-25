package com.pinecone.hydra.umct;

import com.pinecone.framework.system.prototype.Pinenut;

import java.io.IOException;

public class ServiceException extends IOException implements Pinenut {
    public ServiceException() {
        super();
    }

    public ServiceException( String message ) {
        super(message);
    }

    public ServiceException( String message, Throwable cause ) {
        super(message, cause);
    }

    public ServiceException( Throwable cause ) {
        super(cause);
    }
}
