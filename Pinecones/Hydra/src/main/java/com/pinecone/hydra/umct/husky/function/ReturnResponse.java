package com.pinecone.hydra.umct.husky.function;

import com.pinecone.hydra.umct.husky.ResponsePackage;

public interface ReturnResponse<T>  extends ResponsePackage {
    T getReturn();
}
