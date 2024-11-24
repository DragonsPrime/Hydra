package com.pinecone.hydra.umct.protocol.function;

import com.pinecone.hydra.umct.protocol.ArchResponsePackage;

public class GenericReturnResponse<T> extends ArchResponsePackage implements ReturnResponse {
    protected T mReturnTarget;

    public GenericReturnResponse( String szInterceptedPath ) {
        super( szInterceptedPath );
    }

    @Override
    public T getReturn() {
        return this.mReturnTarget;
    }
}
