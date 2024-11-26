package com.pinecone.hydra.storage.volume.entity.local.simple.export.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.channel.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public class TitanSimpleChannelExport64 implements SimpleChannelExport64{
    private VolumeManager           volumeManager;
    private StorageExportIORequest  storageExportIORequest;
    private KChannel                channel;
    public TitanSimpleChannelExport64(SimpleChannelExportEntity entity){
        this.volumeManager       =  entity.getVolumeManager();
        this.storageExportIORequest =  entity.getStorageIORequest();
        this.channel             =  entity.getChannel();
    }

    @Override
    public StorageIOResponse export() throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeManager, this.storageExportIORequest,this.channel);
        return titanDirectChannelExportEntity64.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(this.volumeManager, this.storageExportIORequest,this.channel);
        return titanDirectChannelExportEntity64.export( cacheBlock, offset, endSize, buffer );
    }
}
