package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.MiddleStorageObject;


import java.io.IOException;
import java.sql.SQLException;

public interface SimpleChannelReceiver extends SimpleReceiver {
    MiddleStorageObject channelReceive( ) throws IOException, SQLException;
    MiddleStorageObject channelReceive(Number offset, Number endSize) throws IOException;
}
