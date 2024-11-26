package com.pinecone.hydra.storage.volume.entity.local.simple.export.channel;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.simple.export.SimpleExporter;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface SimpleChannelExport extends SimpleExporter {

    StorageIOResponse export() throws IOException;
    StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer ) throws IOException;
}
