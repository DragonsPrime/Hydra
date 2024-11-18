package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface SimpleChannelExportEntity extends SimpleExporterEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    StorageIOResponse export( ) throws IOException;
    StorageIOResponse raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException;
}
