package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanStripedExportEntity64 extends ArchExportEntity implements StripedExportEntity64 {
    protected StripedVolume     stripedVolume;

    protected StripedExport64   stripedExport;

    public TitanStripedExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, Chanface channel, StripedVolume stripedVolume) {
        super(volumeManager, storageExportIORequest, channel);
        this.stripedVolume = stripedVolume;
        this.stripedExport = new TitanStripedExport64( this );
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.stripedExport.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return null;
    }

    @Override
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }
}
