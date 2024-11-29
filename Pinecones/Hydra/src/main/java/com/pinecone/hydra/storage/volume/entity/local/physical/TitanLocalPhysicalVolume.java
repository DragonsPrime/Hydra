package com.pinecone.hydra.storage.volume.entity.local.physical;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchVolume;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.source.PhysicalVolumeManipulator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    @Override
    public GUID getParent() {
        return this.physicalVolumeManipulator.getParent( this.guid );
    }

    public void setPhysicalVolumeManipulator(PhysicalVolumeManipulator physicalVolumeManipulator ){
        this.physicalVolumeManipulator = physicalVolumeManipulator;
    }


    @Override
    public StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel) throws IOException, SQLException {
//        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager, storageReceiveIORequest, this.mountPoint.getMountPoint(), channel);
//        StorageIOResponse storageIOResponse = titanDirectChannelReceiveEntity64.receive();
//        storageIOResponse.setBottomGuid( this.guid );
//
//        return storageIOResponse;
        return null;
    }

    @Override
    public StorageIOResponse channelReceive(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel, Number offset, Number endSize) throws IOException {
//        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager, storageReceiveIORequest, this.mountPoint.getMountPoint(), channel);
//        StorageIOResponse storageIOResponse = titanDirectChannelReceiveEntity64.receive(offset, endSize);
//        storageIOResponse.setBottomGuid( this.getGuid() );
//        return storageIOResponse;
        return null;
    }

    @Override
    public StorageIOResponse channelExport(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel) throws IOException {
//        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(volumeManager, storageExportIORequest,channel );
//        StorageIOResponse storageIOResponse = titanDirectChannelExportEntity64.export();
//        storageIOResponse.setBottomGuid( this.getGuid() );
//        return storageIOResponse;
        return null;
    }

    @Override
    public StorageIOResponse channelRaid0Export(VolumeManager volumeManager, StorageExportIORequest storageExportIORequest, KChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException {
//        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(volumeManager, storageExportIORequest,channel );
//        StorageIOResponse storageIOResponse = titanDirectChannelExportEntity64.export(cacheBlock, offset, endSize, buffer);
//        storageIOResponse.setBottomGuid( this.getGuid() );
//        return storageIOResponse;
        return null;
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return entity.receive();
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, Number offset, Number endSize) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return entity.receive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer) throws SQLException, IOException {
        return entity.receive( cacheBlock, buffer );
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity) throws SQLException, IOException {
        return entity.export();
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity, Number offset, Number endSize) {
        return null;
    }
}
