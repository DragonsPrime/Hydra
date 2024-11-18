package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.StorageIORequest;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanDirectChannelExportEntity64 extends ArchExportEntity implements DirectChannelExportEntity64{
    private FileChannel             channel;
    private DirectChannelExport     channelExporter;

    public TitanDirectChannelExportEntity64(VolumeManager volumeManager, StorageIORequest storageIORequest, FileChannel channel) {
        super(volumeManager, storageIORequest);
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
    public StorageIOResponse export() throws IOException {
        return this.channelExporter.export( this );
    }

    @Override
    public StorageIOResponse raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) {
        return this.channelExporter.raid0Export( this, cacheBlock, offset, endSize, buffer );
    }

}
