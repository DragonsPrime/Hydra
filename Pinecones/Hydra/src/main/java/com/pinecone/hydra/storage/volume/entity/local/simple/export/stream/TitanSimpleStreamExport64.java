package com.pinecone.hydra.storage.volume.entity.local.simple.export.stream;

import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.stream.TitanDirectStreamExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class TitanSimpleStreamExport64 implements SimpleStreamExport64{
    protected VolumeManager             volumeManager;

    protected StorageExportIORequest    storageExportIORequest;

    protected OutputStream              stream;


    public TitanSimpleStreamExport64( SimpleStreamExportEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.storageExportIORequest = entity.getStorageIORequest();
        this.stream = entity.getStream();
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        TitanDirectStreamExportEntity64 exportEntity = new TitanDirectStreamExportEntity64( this.volumeManager, this.storageExportIORequest, this.stream );
        return exportEntity.export();
    }

    @Override
    public StorageIOResponse export(CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException, SQLException {
        TitanDirectStreamExportEntity64 exportEntity = new TitanDirectStreamExportEntity64( this.volumeManager, this.storageExportIORequest, this.stream );
        return exportEntity.export( cacheBlock, offset, endSize, buffer );
    }
}
