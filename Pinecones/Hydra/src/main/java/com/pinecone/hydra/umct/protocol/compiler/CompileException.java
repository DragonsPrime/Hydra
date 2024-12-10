package com.pinecone.hydra.umct.protocol.compiler;

import com.pinecone.hydra.umct.mapping.InspectException;

public class CompileException extends InspectException {
    public CompileException    () {
        super();
    }

    public CompileException    ( String message ) {
        super(message);
    }

    public CompileException    ( String message, Throwable cause ) {
        super(message, cause);
    }

    public CompileException    ( Throwable cause ) {
        super(cause);
    }

    protected CompileException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
