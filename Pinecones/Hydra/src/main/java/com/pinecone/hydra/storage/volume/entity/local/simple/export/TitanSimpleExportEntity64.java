package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSimpleExportEntity64 extends ArchExportEntity implements SimpleExportEntity64{
    protected SimpleExport64 simpleExportEntity;

    protected SimpleVolume   simpleVolume;

    public TitanSimpleExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, Chanface channel, SimpleVolume simpleVolume) {
        super(volumeManager, storageExportIORequest, channel);
        this.simpleVolume       = simpleVolume;
        this.simpleExportEntity = new TitanSimpleExport64( this );
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.simpleExportEntity.export(this.channel);
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.simpleExportEntity.export( this.channel, cacheBlock, offset, endSize, buffer );
    }

    @Override
    public SimpleVolume getSimpleVolume() {
        return this.simpleVolume;
    }
}
