package com.pinecone.framework.system.executum;

import com.pinecone.framework.system.prototype.Pinenut;

public class StageCompromisedException extends Exception implements Pinenut {
    public StageCompromisedException    () {
        super();
    }

    public StageCompromisedException    ( String message ) {
        super(message);
    }

    public StageCompromisedException    ( String message, Throwable cause ) {
        super(message, cause);
    }

    public StageCompromisedException    ( Throwable cause ) {
        super(cause);
    }

    protected StageCompromisedException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
