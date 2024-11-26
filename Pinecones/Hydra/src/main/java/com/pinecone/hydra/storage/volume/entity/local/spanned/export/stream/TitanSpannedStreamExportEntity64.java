package com.pinecone.hydra.storage.volume.entity.local.spanned.export.stream;

import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class TitanSpannedStreamExportEntity64 extends ArchExportEntity implements SpannedStreamExportEntity64{
    protected OutputStream              stream;

    protected SpannedStreamExport64     streamExport;

    public TitanSpannedStreamExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, OutputStream stream, SpannedVolume volume) {
        super(volumeManager, storageExportIORequest);
        this.stream  =  stream;
        this.streamExport = new TitanSpannedStreamExport64( this, volume );
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.streamExport.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return null;
    }

    @Override
    public OutputStream getStream() {
        return this.stream;
    }

    @Override
    public void setStream(OutputStream stream) {
        this.stream = stream;
    }
}
