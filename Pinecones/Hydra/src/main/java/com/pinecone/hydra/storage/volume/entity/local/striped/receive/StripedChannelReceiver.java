package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.StorageIOResponse;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedChannelReceiver extends StripedReceiver{
    StorageIOResponse channelReceive( ) throws IOException, SQLException;
    StorageIOResponse channelReceive(Number offset, Number endSize) throws IOException, SQLException;
}
