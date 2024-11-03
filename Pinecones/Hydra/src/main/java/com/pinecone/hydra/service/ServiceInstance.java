package com.pinecone.hydra.service;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.Identification;

public interface ServiceInstance extends Pinenut {
    Identification getId();

    Object getProcessObject();
}
