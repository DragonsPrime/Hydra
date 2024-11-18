package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectChannelExport extends DirectExport{
    StorageIOResponse export(DirectChannelExportEntity entity ) throws IOException;

    //MiddleStorageObject raid0Export(DirectChannelExportEntity entity, byte[] buffer, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity);
    StorageIOResponse raid0Export(DirectChannelExportEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer);
}
