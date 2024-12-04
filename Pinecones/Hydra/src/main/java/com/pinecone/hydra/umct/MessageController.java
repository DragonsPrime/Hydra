package com.pinecone.hydra.umct;

import com.pinecone.framework.system.prototype.Pinenut;

public interface MessageController extends Pinenut {
    String getAddressMapping();

    void invoke( Object... args ) throws Exception;
}
