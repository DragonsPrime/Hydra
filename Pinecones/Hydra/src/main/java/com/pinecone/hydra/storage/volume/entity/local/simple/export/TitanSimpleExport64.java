package com.pinecone.hydra.storage.volume.entity.local.simple.export;

import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSimpleExport64 implements SimpleExport64{
    private VolumeManager           volumeManager;

    private StorageExportIORequest  storageExportIORequest;

    private SimpleVolume             simpleVolume;

    private KenVolumeFileSystem      kenVolumeFileSystem;

    public TitanSimpleExport64( SimpleExportEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.storageExportIORequest = entity.getStorageIORequest();
        this.simpleVolume = entity.getSimpleVolume();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }
    @Override
    public StorageIOResponse export(Chanface chanface) throws IOException, SQLException {
        SQLiteExecutor sqLiteExecutor = simpleVolume.getSQLiteExecutor();
        String sourceName = this.kenVolumeFileSystem.getSimpleStorageObjectSourceName(this.storageExportIORequest.getStorageObjectGuid(), sqLiteExecutor);
        this.storageExportIORequest.setSourceName(sourceName);
        TitanDirectExportEntity64 exportEntity = new TitanDirectExportEntity64( this.volumeManager, this.storageExportIORequest, chanface );
        return exportEntity.export();
    }

    @Override
    public StorageIOResponse export(RandomAccessChanface randomAccessChanface) throws IOException, SQLException {
        return null;
    }

    @Override
    public StorageIOResponse export(Chanface chanface,CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        TitanDirectExportEntity64 exportEntity = new TitanDirectExportEntity64( this.volumeManager, this.storageExportIORequest, chanface );
        return exportEntity.export( cacheBlock, offset, endSize, buffer );
    }
}
