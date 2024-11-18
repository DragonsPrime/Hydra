package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface DirectChannelExportEntity extends DirectExportEntity{
    FileChannel getChannel();

    void setChannel( FileChannel channel );

    StorageIOResponse export() throws IOException;
    StorageIOResponse raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer);
}
