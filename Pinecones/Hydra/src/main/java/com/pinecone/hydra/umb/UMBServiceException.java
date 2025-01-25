package com.pinecone.hydra.umb;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umct.ServiceException;

public class UMBServiceException extends ServiceException implements Pinenut {
    public UMBServiceException() {
        super();
    }

    public UMBServiceException( String message ) {
        super(message);
    }

    public UMBServiceException( String message, Throwable cause ) {
        super(message, cause);
    }

    public UMBServiceException( Throwable cause ) {
        super(cause);
    }
}
