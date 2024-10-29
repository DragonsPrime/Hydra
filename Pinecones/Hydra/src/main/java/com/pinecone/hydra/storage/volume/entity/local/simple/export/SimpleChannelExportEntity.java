package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface SimpleChannelExportEntity extends SimpleExporterEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    void export( ) throws IOException;
}
