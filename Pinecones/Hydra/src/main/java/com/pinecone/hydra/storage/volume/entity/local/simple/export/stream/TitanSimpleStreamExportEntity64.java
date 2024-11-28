package com.pinecone.hydra.storage.volume.entity.local.simple.export.stream;

import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class TitanSimpleStreamExportEntity64 extends ArchExportEntity implements SimpleStreamExportEntity64{
    protected OutputStream          stream;

    protected SimpleStreamExport64  simpleStreamExport64;

    public TitanSimpleStreamExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest) {
        super(volumeManager, storageExportIORequest,null);
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return null;
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
