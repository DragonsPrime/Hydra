package com.pinecone.hydra.umct.protocol.function;

import com.pinecone.hydra.umct.protocol.ResponsePackage;

public interface ReturnResponse<T>  extends ResponsePackage {
    T getReturn();
}
