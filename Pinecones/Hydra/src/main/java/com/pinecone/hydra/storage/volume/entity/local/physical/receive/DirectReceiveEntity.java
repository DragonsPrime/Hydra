package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.IOException;

public interface DirectReceiveEntity extends ReceiveEntity {
    MiddleStorageObject receive() throws IOException;

    MiddleStorageObject receive( Number offset, Number endSize ) throws IOException;
}
