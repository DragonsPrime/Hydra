package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;

import java.io.IOException;
import java.sql.SQLException;

public interface ISpannedReceive extends Receiver {
    StorageIOResponse receive() throws IOException, SQLException;
    StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException;
}
