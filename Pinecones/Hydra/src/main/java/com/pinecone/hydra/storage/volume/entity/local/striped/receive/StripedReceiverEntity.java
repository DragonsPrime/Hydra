package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedReceiverEntity extends ReceiveEntity {
    MiddleStorageObject receive() throws IOException, SQLException;
    MiddleStorageObject receive( Number offset, Number endSize ) throws IOException;
}
