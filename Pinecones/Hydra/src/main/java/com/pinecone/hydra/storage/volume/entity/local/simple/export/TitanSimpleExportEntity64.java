package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.hydra.storage.KChannel;
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

    public TitanSimpleExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel, SimpleVolume simpleVolume) {
        super(volumeManager, storageExportIORequest, channel);
        this.simpleExportEntity = new TitanSimpleExport64( this );
        this.simpleVolume       = simpleVolume;
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.simpleExportEntity.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.simpleExportEntity.export( cacheBlock, offset, endSize, buffer );
    }
}
