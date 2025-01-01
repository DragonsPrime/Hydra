package com.pinecone.hydra.umct;

import com.pinecone.framework.system.prototype.Pinenut;

public class ServiceInternalException extends ServiceException implements Pinenut {
    public ServiceInternalException() {
        super();
    }

    public ServiceInternalException( String message ) {
        super(message);
    }

    public ServiceInternalException( String message, Throwable cause ) {
        super(message, cause);
    }

    public ServiceInternalException( Throwable cause ) {
        super(cause);
    }
}
