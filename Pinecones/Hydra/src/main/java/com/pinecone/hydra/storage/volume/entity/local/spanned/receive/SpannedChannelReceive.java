package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.StorageIOResponse;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedChannelReceive extends SpannedReceive{
    StorageIOResponse receive() throws IOException, SQLException;
    StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException;
}
