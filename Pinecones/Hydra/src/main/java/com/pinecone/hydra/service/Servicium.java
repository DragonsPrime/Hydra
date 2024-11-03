package com.pinecone.hydra.service;

import com.pinecone.framework.system.executum.Processum;

public interface Servicium extends ServiceInstance {
    @Override
    Processum getProcessObject();
}
