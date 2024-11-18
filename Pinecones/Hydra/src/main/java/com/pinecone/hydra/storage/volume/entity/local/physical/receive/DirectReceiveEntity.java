package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.IOException;

public interface DirectReceiveEntity extends ReceiveEntity {
    StorageIOResponse receive() throws IOException;

    StorageIOResponse receive(Number offset, Number endSize ) throws IOException;
}
