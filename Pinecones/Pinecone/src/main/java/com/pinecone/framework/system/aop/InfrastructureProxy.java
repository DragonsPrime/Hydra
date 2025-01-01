package com.pinecone.framework.system.aop;

import com.pinecone.framework.system.prototype.Pinenut;

public interface InfrastructureProxy extends Pinenut {
    Object getWrappedObject();
}
