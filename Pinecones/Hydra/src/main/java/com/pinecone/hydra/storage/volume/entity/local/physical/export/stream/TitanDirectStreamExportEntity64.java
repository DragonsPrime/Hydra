package com.pinecone.hydra.storage.volume.entity.local.physical.export.stream;

import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchExportEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class TitanDirectStreamExportEntity64 extends ArchExportEntity implements DirectStreamExportEntity64{
    protected OutputStream  stream;

    protected DirectStreamExport    streamExport;

    public TitanDirectStreamExportEntity64(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, OutputStream stream) {
        super(volumeManager, storageExportIORequest,null);
        this.stream = stream;
        this.streamExport = new TitanDirectStreamExport64();
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        return this.streamExport.export( this );
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        return this.streamExport.export( this, cacheBlock,offset,endSize,buffer );
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
