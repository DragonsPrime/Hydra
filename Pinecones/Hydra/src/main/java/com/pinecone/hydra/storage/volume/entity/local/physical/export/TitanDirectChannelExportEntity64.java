package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanDirectChannelExportEntity64 extends ArchExportEntity implements DirectChannelExportEntity64{
    private FileChannel             channel;
    private DirectChannelExport     channelExporter;

    public TitanDirectChannelExportEntity64(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel) {
        super(volumeManager, exportStorageObject);
        this.channel = channel;
        this.channelExporter = new TitanDirectChannelExport64();
    }


    @Override
    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    @Override
    public MiddleStorageObject export() throws IOException {
        return this.channelExporter.export( this );
    }

}
