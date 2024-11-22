package com.pinecone.hydra.storage.file.transmit.exporter.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.exporter.ArchExporterEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class GenericChannelExporterEntity extends ArchExporterEntity implements ChannelExporterEntity{
    private KChannel channel;
    private ChannelExporter     channelExporter;

    public GenericChannelExporterEntity(KOMFileSystem fileSystem, FileNode file, KChannel channel) {
        super(fileSystem, file);
        this.channel = channel;
        this.channelExporter = new GenericChannelExporter();
    }

    @Override
    public KChannel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(KChannel channel) {
        this.channel = channel;
    }

    @Override
    public void export( UniformVolumeManager volumeManager ) throws IOException, SQLException {
        this.channelExporter.export(this, volumeManager);
    }
}
