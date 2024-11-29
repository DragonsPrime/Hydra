package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface SpannedReceive extends Receiver {
    StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
