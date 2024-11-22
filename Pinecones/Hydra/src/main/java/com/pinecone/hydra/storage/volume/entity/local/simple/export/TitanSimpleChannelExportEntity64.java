package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public class TitanSimpleChannelExportEntity64 extends ArchExportEntity implements SimpleChannelExportEntity64{
    private KChannel                    channel;
    private SimpleChannelExport64       simpleChannelExport64;
    public TitanSimpleChannelExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel) {
        super(volumeManager, storageExportIORequest);
        this.channel = channel;
        this.simpleChannelExport64 = new TitanSimpleChannelExport64( this );
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
        return this.simpleChannelExport64.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.simpleChannelExport64.export( cacheBlock, offset, endSize, buffer );

    }
}
