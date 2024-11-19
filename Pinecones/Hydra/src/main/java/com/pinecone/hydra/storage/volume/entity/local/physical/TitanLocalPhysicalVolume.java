package com.pinecone.hydra.storage.volume.entity.local.physical;

import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchVolume;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel.TitanDirectChannelReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.source.PhysicalVolumeManipulator;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanLocalPhysicalVolume extends ArchVolume implements LocalPhysicalVolume {
    private MountPoint                  mountPoint;
    private PhysicalVolumeManipulator   physicalVolumeManipulator;

    public TitanLocalPhysicalVolume(VolumeManager volumeManager, PhysicalVolumeManipulator physicalVolumeManipulator) {
        super(volumeManager);
        this.physicalVolumeManipulator = physicalVolumeManipulator;
    }
    public TitanLocalPhysicalVolume(){}


    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }


    @Override
    public MountPoint getMountPoint() {
        return this.mountPoint;
    }

    @Override
    public void setMountPoint(MountPoint mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setPhysicalVolumeManipulator( PhysicalVolumeManipulator physicalVolumeManipulator ){
        this.physicalVolumeManipulator = physicalVolumeManipulator;
    }


    @Override
    public StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, FileChannel channel) throws IOException, SQLException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager, storageReceiveIORequest, this.mountPoint.getMountPoint(), channel);
        StorageIOResponse storageIOResponse = titanDirectChannelReceiveEntity64.receive();
        storageIOResponse.setBottomGuid( this.guid );

        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, FileChannel channel, Number offset, Number endSize) throws IOException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager, storageReceiveIORequest, this.mountPoint.getMountPoint(), channel);
        StorageIOResponse storageIOResponse = titanDirectChannelReceiveEntity64.receive(offset, endSize);
        storageIOResponse.setBottomGuid( this.getGuid() );
        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelExport(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, FileChannel channel) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(volumeManager, storageExportIORequest,channel );
        StorageIOResponse storageIOResponse = titanDirectChannelExportEntity64.export();
        storageIOResponse.setBottomGuid( this.getGuid() );
        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelRaid0Export(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, FileChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(volumeManager, storageExportIORequest,channel );
        StorageIOResponse storageIOResponse = titanDirectChannelExportEntity64.raid0Export(cacheBlock, offset, endSize, buffer);
        storageIOResponse.setBottomGuid( this.getGuid() );
        return storageIOResponse;
    }
}
