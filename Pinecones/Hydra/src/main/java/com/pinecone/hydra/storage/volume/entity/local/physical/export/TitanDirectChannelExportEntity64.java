package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public class TitanDirectChannelExportEntity64 extends ArchExportEntity implements DirectChannelExportEntity64{
    private KChannel                channel;
    private DirectChannelExport     channelExporter;

    public TitanDirectChannelExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel) {
        super(volumeManager, storageExportIORequest);
        this.channel = channel;
        this.channelExporter = new TitanDirectChannelExport64();

    }


    @Override
    public KChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(KChannel channel) {
        this.channel = channel;
    }

    @Override
    public StorageIOResponse export() throws IOException {
        return this.channelExporter.export( this );
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) {
        return this.channelExporter.export( this, cacheBlock, offset, endSize, buffer );
    }

}
