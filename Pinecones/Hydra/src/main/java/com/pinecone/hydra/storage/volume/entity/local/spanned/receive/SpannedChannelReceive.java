package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleChannelReceiverEntity;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedChannelReceive extends SpannedReceive{
    MiddleStorageObject receive() throws IOException, SQLException;
    MiddleStorageObject receive( Number offset, Number endSize) throws IOException, SQLException;
}
