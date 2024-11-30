package com.pinecone.hydra.storage.volume.entity.local.physical.export;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanDirectExportEntity64 extends ArchExportEntity implements DirectExportEntity64{
    protected DirectExport64 directExport;

    public TitanDirectExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, Chanface channel) {
        super(volumeManager, storageExportIORequest, channel);
        this.directExport = new TitanDirectExport64( this );
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.directExport.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.directExport.export(cacheBlock, offset, endSize, buffer);
    }
}
