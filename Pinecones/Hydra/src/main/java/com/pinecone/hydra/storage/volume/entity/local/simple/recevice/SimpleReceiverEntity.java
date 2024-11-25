package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface SimpleReceiverEntity extends ReceiveEntity {
    StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException;
}
