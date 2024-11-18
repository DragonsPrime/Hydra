package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageIORequest;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanSimpleChannelExport64 implements SimpleChannelExport64{
    private VolumeManager           volumeManager;
    private StorageIORequest storageIORequest;
    private FileChannel             channel;
    public TitanSimpleChannelExport64(SimpleChannelExportEntity entity){
        this.volumeManager       =  entity.getVolumeManager();
        this.storageIORequest =  entity.getStorageIORequest();
        this.channel             =  entity.getChannel();
    }

    @Override
    public StorageIOResponse export() throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeManager, this.storageIORequest,this.channel);
        return titanDirectChannelExportEntity64.export();
    }

    @Override
    public StorageIOResponse raid0Export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeManager, this.storageIORequest,this.channel);
        return titanDirectChannelExportEntity64.raid0Export( cacheBlock, offset, endSize, buffer );
    }
}
