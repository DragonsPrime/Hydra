package com.pinecone.framework.system.executum;

import com.pinecone.framework.system.prototype.Pinenut;

public class JobCompromisedException extends Exception implements Pinenut {
    public JobCompromisedException    () {
        super();
    }

    public JobCompromisedException    ( String message ) {
        super(message);
    }

    public JobCompromisedException    ( String message, Throwable cause ) {
        super(message, cause);
    }

    public JobCompromisedException    ( Throwable cause ) {
        super(cause);
    }

    protected JobCompromisedException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
