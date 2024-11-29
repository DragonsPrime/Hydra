package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Exporter;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectExport extends Exporter {
    StorageIOResponse export() throws IOException;

    StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer);
}
