package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface PhysicalVolume extends Volume{
    MountPoint getMountPoint();
    void setMountPoint( MountPoint mountPoint );

    GUID getParent();

    StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel ) throws IOException, SQLException;
    StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel, Number offset, Number endSize ) throws IOException;

    StorageIOResponse channelExport(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel ) throws IOException;
    StorageIOResponse channelRaid0Export(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException;

    StorageIOResponse receive( ReceiveEntity entity ) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive( ReceiveEntity entity, Number offset, Number endSize ) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException;

    StorageIOResponse receive( ReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer ) throws SQLException, IOException;

    StorageIOResponse export( ExporterEntity entity ) throws SQLException, IOException;
    //敬请期待
    StorageIOResponse export( ExporterEntity entity, Number offset, Number endSize );

}
