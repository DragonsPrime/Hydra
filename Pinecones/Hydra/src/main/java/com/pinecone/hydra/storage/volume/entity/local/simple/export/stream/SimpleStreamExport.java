package com.pinecone.hydra.storage.volume.entity.local.simple.export.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.simple.export.SimpleExporter;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public interface SimpleStreamExport extends SimpleExporter {
    StorageIOResponse export() throws IOException, SQLException;
    StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer ) throws IOException, SQLException;
}
