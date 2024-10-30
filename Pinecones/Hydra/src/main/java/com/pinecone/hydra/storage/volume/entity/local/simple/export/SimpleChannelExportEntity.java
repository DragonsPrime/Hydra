package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.MiddleStorageObject;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface SimpleChannelExportEntity extends SimpleExporterEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    MiddleStorageObject export( ) throws IOException;
}
