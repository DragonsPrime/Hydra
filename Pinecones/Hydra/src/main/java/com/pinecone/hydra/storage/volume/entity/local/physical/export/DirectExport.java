package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Exporter;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectExport extends Exporter {
    StorageIOResponse export(Chanface chanface) throws IOException;

    StorageIOResponse export(RandomAccessChanface randomAccessChanface) throws IOException;

    StorageIOResponse export( CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer);
}
