package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;

public interface UniformFile extends Pinenut {
    String getName();

    long size();
}
