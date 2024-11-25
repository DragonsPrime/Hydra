package com.pinecone.hydra.storage.volume.entity.local.spanned.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.SpannedReceive;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface SpannedStreamReceive extends SpannedReceive {
    StorageIOResponse streamReceive( ) throws IOException, SQLException;

    StorageIOResponse streamReceive(Number offset, Number endSize) throws IOException, SQLException;

    StorageIOResponse streamReceive(CacheBlock cacheBlock, byte[] buffer ) throws IOException, SQLException;
}
