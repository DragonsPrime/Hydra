package com.pinecone.hydra.storage.file.transmit.exporter.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.file.transmit.exporter.ExporterEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface ChannelExporterEntity extends ExporterEntity {
    KChannel getChannel();

    void setChannel( KChannel channel );

    @Override
    default ChannelExporterEntity evinceChannelExporterEntity() {
        return this;
    }


}
