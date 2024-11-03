package com.pinecone.hydra.storage.file.transmit.exporter.channel;

import com.pinecone.hydra.storage.file.transmit.exporter.ExporterEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface ChannelExporterEntity extends ExporterEntity {
    FileChannel getChannel();

    void setChannel( FileChannel channel );

    @Override
    default ChannelExporterEntity evinceChannelExporterEntity() {
        return this;
    }

    void export( ) throws IOException;
}
