package com.pinecone.framework.system.executum;

import com.pinecone.framework.system.prototype.Pinenut;

public class TaskCompromisedException extends Exception implements Pinenut {
    public TaskCompromisedException    () {
        super();
    }

    public TaskCompromisedException    ( String message ) {
        super(message);
    }

    public TaskCompromisedException    ( String message, Throwable cause ) {
        super(message, cause);
    }

    public TaskCompromisedException    ( Throwable cause ) {
        super(cause);
    }

    protected TaskCompromisedException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}