package com.pinecone.hydra.storage.volume.entity.local.physical;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
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
    public void channelExport( KOMFileSystem fileSystem, FileNode file ) throws IOException {
        File temporaryFile = new File(this.mountPoint.getMountPoint());
        FileChannel channel = FileChannel.open(temporaryFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        ChannelExporterEntity exporterEntity = new GenericChannelExporterEntity(fileSystem, file, channel);
        exporterEntity.export();
    }

    @Override
    public void streamExport( KOMFileSystem fileSystem, FileNode file ) throws IOException {
        File temporaryFile = new File(this.mountPoint.getMountPoint());
        FileOutputStream fileOutputStream = new FileOutputStream(temporaryFile);
        StreamExporterEntity exporterEntity = new GenericStreamExporterEntity(fileSystem, file, fileOutputStream);
        exporterEntity.export();
    }

    @Override
    public void channelReceive( KOMFileSystem fileSystem, FileNode file, FileChannel channel ) throws IOException {
        ChannelReceiverEntity receiveEntity = new GenericChannelReceiveEntity(fileSystem, this.mountPoint.getMountPoint(), file, channel);
        receiveEntity.receive();
    }

    @Override
    public void channelReceive( KOMFileSystem fileSystem, FileNode file, FileChannel channel, Number offset, Number endSize ) throws IOException {
        ChannelReceiverEntity receiveEntity = new GenericChannelReceiveEntity(fileSystem, this.mountPoint.getMountPoint(), file, channel);
        receiveEntity.receive( offset,endSize );
    }

    @Override
    public void channelReceive( KOMFileSystem fileSystem, FileNode file,FileChannel channel, GUID frameGuid, int threadNum, int threadId ) throws IOException {
        ChannelReceiverEntity receiveEntity = new GenericChannelReceiveEntity(fileSystem, this.mountPoint.getMountPoint(), file, channel);
        receiveEntity.receive( frameGuid,threadId,threadNum );
    }

    @Override
    public void streamReceive( KOMFileSystem fileSystem, FileNode file, InputStream inputStream ) throws IOException {
        StreamReceiverEntity receiverEntity = new GenericStreamReceiverEntity(fileSystem, this.mountPoint.getMountPoint(), file, inputStream);
        receiverEntity.receive();
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
    public MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel) throws IOException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeTree, receiveStorageObject, this.mountPoint.getMountPoint(), channel);
        MiddleStorageObject middleStorageObject = titanDirectChannelReceiveEntity64.receive();
        middleStorageObject.setBottomGuid( this.guid );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelReceive(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, FileChannel channel, Number offset, Number endSize) throws IOException {
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeTree, receiveStorageObject, this.mountPoint.getMountPoint(), channel);
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
