package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedReceiverEntity extends ReceiveEntity {
    StorageIOResponse receive() throws IOException, SQLException;
    StorageIOResponse receive(Number offset, Number endSize ) throws IOException;
}
