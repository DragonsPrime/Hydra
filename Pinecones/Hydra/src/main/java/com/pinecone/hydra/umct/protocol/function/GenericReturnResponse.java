package com.pinecone.hydra.umct.protocol.function;

import com.pinecone.hydra.umct.protocol.ArchResponsePackage;

public class GenericReturnResponse<T> extends ArchResponsePackage implements ReturnResponse {
    protected T mReturnTarget;

    public GenericReturnResponse( String szInterceptedPath, T returnVal ) {
        super( szInterceptedPath );

        this.mReturnTarget = returnVal;
    }

    @Override
    public T getReturn() {
        return this.mReturnTarget;
    }
}
