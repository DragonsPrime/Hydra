package com.pinecone.hydra.umct;

import com.pinecone.framework.system.prototype.Pinenut;

public class IlleagalResponseException extends ServiceException implements Pinenut {
    public IlleagalResponseException() {
        super();
    }

    public IlleagalResponseException( String message ) {
        super(message);
    }

    public IlleagalResponseException( String message, Throwable cause ) {
        super(message, cause);
    }

    public IlleagalResponseException( Throwable cause ) {
        super(cause);
    }
}
