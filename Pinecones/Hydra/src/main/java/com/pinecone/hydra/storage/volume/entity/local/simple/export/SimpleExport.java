package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Exporter;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface SimpleExport extends Exporter {
    StorageIOResponse export() throws IOException, SQLException;
    StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer ) throws IOException;
}
