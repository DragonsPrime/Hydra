package com.pinecone.hydra.storage.volume.runtime;

import com.pinecone.framework.system.executum.JobCompromisedException;

public class VolumeJobCompromiseException extends JobCompromisedException {
    public VolumeJobCompromiseException    () {
        super();
    }

    public VolumeJobCompromiseException    ( String message ) {
        super(message);
    }

    public VolumeJobCompromiseException    ( String message, Throwable cause ) {
        super(message, cause);
    }

    public VolumeJobCompromiseException    ( Throwable cause ) {
        super(cause);
    }

    protected VolumeJobCompromiseException ( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
