package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.StorageIORequest;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanSimpleChannelExportEntity64 extends ArchExportEntity implements SimpleChannelExportEntity64{
    private FileChannel                 channel;
    private SimpleChannelExport64       simpleChannelExport64;
    public TitanSimpleChannelExportEntity64(VolumeManager volumeManager, StorageIORequest storageIORequest, FileChannel channel) {
        super(volumeManager, storageIORequest);
        this.channel = channel;
        this.simpleChannelExport64 = new TitanSimpleChannelExport64( this );
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
        return this.simpleChannelExport64.export();
    }

    @Override
    public StorageIOResponse raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.simpleChannelExport64.raid0Export( cacheBlock, offset, endSize, buffer );

    }
}
