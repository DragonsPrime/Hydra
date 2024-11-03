package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ReceiveStorageObject extends Pinenut {
    String getName();
    void setName( String name );

    Number getSize();
    void setSize( Number size );
}
