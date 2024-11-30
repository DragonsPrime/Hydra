package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSimpleExport64 implements SimpleExport64{
    private VolumeManager volumeManager;

    private StorageExportIORequest storageExportIORequest;

    private Chanface channel;

    public TitanSimpleExport64( SimpleExportEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.storageExportIORequest = entity.getStorageIORequest();
        this.channel = entity.getChannel();
    }
    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        TitanDirectExportEntity64 exportEntity = new TitanDirectExportEntity64( this.volumeManager, this.storageExportIORequest, this.channel );
        return exportEntity.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        TitanDirectExportEntity64 exportEntity = new TitanDirectExportEntity64( this.volumeManager, this.storageExportIORequest, this.channel );
        return exportEntity.export( cacheBlock, offset, endSize, buffer );
    }
}
