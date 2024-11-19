package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface PhysicalVolume extends Volume{
    MountPoint getMountPoint();
    void setMountPoint( MountPoint mountPoint );

    StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, FileChannel channel ) throws IOException, SQLException;
    StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, FileChannel channel, Number offset, Number endSize ) throws IOException;

    StorageIOResponse channelExport(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, FileChannel channel ) throws IOException;
    StorageIOResponse channelRaid0Export(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, FileChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException;

}
