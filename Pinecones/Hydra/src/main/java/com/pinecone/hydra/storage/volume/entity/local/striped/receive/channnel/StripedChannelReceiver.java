package com.pinecone.hydra.storage.volume.entity.local.striped.receive.channnel;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.StripedReceiver;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedChannelReceiver extends StripedReceiver {
    StorageIOResponse channelReceive( ) throws IOException, SQLException;
    StorageIOResponse channelReceive(Number offset, Number endSize) throws IOException, SQLException;
}
