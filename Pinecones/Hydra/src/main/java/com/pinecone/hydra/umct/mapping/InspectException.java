package com.pinecone.hydra.umct.mapping;

import com.pinecone.framework.system.PineRuntimeException;

public class InspectException extends PineRuntimeException {
    public InspectException    () {
        super();
    }

    public InspectException    ( String message ) {
        super(message);
    }

    public InspectException    ( String message, Throwable cause ) {
        super(message, cause);
    }

    public InspectException    ( Throwable cause ) {
        super(cause);
    }

    protected InspectException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
