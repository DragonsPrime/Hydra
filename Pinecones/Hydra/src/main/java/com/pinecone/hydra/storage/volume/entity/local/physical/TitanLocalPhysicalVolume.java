package com.pinecone.hydra.storage.volume.entity.local.physical;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.framework.util.sqlite.SQLiteMethod;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.exporter.channel.ChannelExporterEntity;
import com.pinecone.hydra.storage.file.transmit.exporter.channel.GenericChannelExporterEntity;
import com.pinecone.hydra.storage.file.transmit.exporter.stream.GenericStreamExporterEntity;
import com.pinecone.hydra.storage.file.transmit.exporter.stream.StreamExporterEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.ChannelReceiverEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.GenericChannelReceiveEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.stream.GenericStreamReceiverEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.stream.StreamReceiverEntity;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.TitanDirectChannelReceiveEntity64;
import com.pinecone.hydra.storage.volume.source.PhysicalVolumeManipulator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

public class TitanLocalPhysicalVolume extends ArchVolume implements LocalPhysicalVolume {
    private MountPoint                  mountPoint;
    private PhysicalVolumeManipulator   physicalVolumeManipulator;

    public TitanLocalPhysicalVolume(VolumeTree volumeTree, PhysicalVolumeManipulator physicalVolumeManipulator) {
        super(volumeTree);
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
    public MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath) throws IOException, SQLException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeTree, receiveStorageObject, this.mountPoint.getMountPoint()+"\\"+destDirPath, channel);
        MiddleStorageObject middleStorageObject = titanDirectChannelReceiveEntity64.receive();
        middleStorageObject.setBottomGuid( this.guid );

        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath,  Number offset, Number endSize) throws IOException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeTree, receiveStorageObject, this.mountPoint.getMountPoint()+"\\"+destDirPath, channel);
        MiddleStorageObject middleStorageObject = titanDirectChannelReceiveEntity64.receive(offset, endSize);
        middleStorageObject.setBottomGuid( this.getGuid() );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelExport(VolumeTree volumeTree, ExportStorageObject exportStorageObject, FileChannel channel) throws IOException {
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64( volumeTree, exportStorageObject,channel );
        MiddleStorageObject middleStorageObject = titanDirectChannelExportEntity64.export();
        middleStorageObject.setBottomGuid( this.getGuid() );
        return middleStorageObject;
    }
}
