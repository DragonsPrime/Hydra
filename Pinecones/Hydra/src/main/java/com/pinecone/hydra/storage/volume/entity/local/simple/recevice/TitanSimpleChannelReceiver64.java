package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.TitanDirectChannelReceiveEntity64;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class TitanSimpleChannelReceiver64 implements SimpleChannelReceiver64{
    private SimpleVolume            simpleVolume;
    private FileChannel             fileChannel;
    private VolumeTree              volumeTree;
    private ReceiveStorageObject    receiveStorageObject;

    public TitanSimpleChannelReceiver64( SimpleChannelReceiverEntity entity ){
        this.volumeTree = entity.getVolumeTree();
        this.simpleVolume = entity.getSimpleVolume();
        this.fileChannel = entity.getChannel();
        this.receiveStorageObject = entity.getReceiveStorageObject();
    }
    @Override
    public MiddleStorageObject receive() throws IOException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(guids.get(0));

        return physicalVolume.channelReceive( this.volumeTree,this.receiveStorageObject,this.fileChannel );
    }

    @Override
    public MiddleStorageObject receive(Number offset, Number endSize) throws IOException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(guids.get(0));
        return physicalVolume.channelReceive( this.volumeTree,this.receiveStorageObject,this.fileChannel,offset,endSize );
    }
}
