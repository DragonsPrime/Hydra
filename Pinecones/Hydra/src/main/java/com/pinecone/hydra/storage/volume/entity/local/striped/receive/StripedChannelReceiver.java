package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;
import java.sql.SQLException;

public interface StripedChannelReceiver extends StripedReceiver{
    MiddleStorageObject channelReceive( ) throws IOException, SQLException;
    MiddleStorageObject channelReceive(Number offset, Number endSize) throws IOException;
}
