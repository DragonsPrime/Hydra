package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface ISimpleReceive extends Receiver {
    StorageIOResponse receive( ) throws IOException, SQLException;
    StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException;
    StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer ) throws IOException, SQLException;
}
