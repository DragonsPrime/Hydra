package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanSimpleChannelReceiver64   implements SimpleChannelReceiver64{
    private SimpleVolume            simpleVolume;
    private FileChannel             fileChannel;
    private VolumeManager           volumeManager;
    private ReceiveStorageObject    receiveStorageObject;

    public TitanSimpleChannelReceiver64( SimpleChannelReceiverEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.simpleVolume = entity.getSimpleVolume();
        this.fileChannel = entity.getChannel();
        this.receiveStorageObject = entity.getReceiveStorageObject();
    }
    @Override
    public StorageIOResponse channelReceive() throws IOException, SQLException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));

        return physicalVolume.channelReceive( this.volumeManager,this.receiveStorageObject,this.fileChannel );
    }

    @Override
    public StorageIOResponse channelReceive(Number offset, Number endSize) throws IOException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        return physicalVolume.channelReceive( this.volumeManager,this.receiveStorageObject,this.fileChannel, offset,endSize );
    }
}
