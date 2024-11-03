package com.pinecone.hydra.storage.volume.entity.local.physical;

import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.TitanDirectChannelReceiveEntity64;
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
    public MiddleStorageObject channelReceive(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath) throws IOException, SQLException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager, receiveStorageObject, this.mountPoint.getMountPoint()+"\\"+destDirPath, channel);
        MiddleStorageObject middleStorageObject = titanDirectChannelReceiveEntity64.receive();
        middleStorageObject.setBottomGuid( this.guid );

        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelReceive(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath, Number offset, Number endSize) throws IOException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager, receiveStorageObject, this.mountPoint.getMountPoint()+"\\"+destDirPath, channel);
        MiddleStorageObject middleStorageObject = titanDirectChannelReceiveEntity64.receive(offset, endSize);
        middleStorageObject.setBottomGuid( this.getGuid() );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelExport(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(volumeManager, exportStorageObject,channel );
        MiddleStorageObject middleStorageObject = titanDirectChannelExportEntity64.export();
        middleStorageObject.setBottomGuid( this.getGuid() );
        return middleStorageObject;
    }
}
