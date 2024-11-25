package com.pinecone.hydra.storage.volume.entity.local.striped.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.StripedReceiver;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedStreamReceive extends StripedReceiver {
    StorageIOResponse streamReceive( ) throws IOException, SQLException;

    StorageIOResponse streamReceive( Number offset, Number endSize ) throws IOException, SQLException;
}
