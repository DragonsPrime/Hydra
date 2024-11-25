package com.pinecone.hydra.storage.volume.entity.local.physical.export.channel;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.DirectExport;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectChannelExport extends DirectExport {
    StorageIOResponse export(DirectChannelExportEntity entity ) throws IOException;

    StorageIOResponse export(DirectChannelExportEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer);
}
