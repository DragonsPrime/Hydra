package com.pinecone.hydra.storage.volume.entity.local.physical.export.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.DirectExport;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectStreamExport extends DirectExport {

    StorageIOResponse export(DirectStreamExportEntity entity ) throws IOException;

    StorageIOResponse export(DirectStreamExportEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer);

}
