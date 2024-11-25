package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleReceiver;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface SimpleStreamReceiver extends SimpleReceiver {
    StorageIOResponse streamReceive( ) throws IOException, SQLException;

    StorageIOResponse streamReceive(Number offset, Number endSize) throws IOException, SQLException;

    StorageIOResponse streamReceive( CacheBlock cacheBlock, byte[] buffer ) throws IOException, SQLException;
}
