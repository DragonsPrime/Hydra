package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.StorageIOResponse;


import java.io.IOException;
import java.sql.SQLException;

public interface SimpleChannelReceiver extends SimpleReceiver {
    StorageIOResponse channelReceive( ) throws IOException, SQLException;
    StorageIOResponse channelReceive(Number offset, Number endSize) throws IOException;
}
