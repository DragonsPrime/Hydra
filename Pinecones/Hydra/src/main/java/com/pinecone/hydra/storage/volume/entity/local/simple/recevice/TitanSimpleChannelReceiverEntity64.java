package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanSimpleChannelReceiverEntity64 extends ArchReceiveEntity implements SimpleChannelReceiverEntity64{

    private FileChannel channel;
    private SimpleVolume simpleVolume;
    private SimpleChannelReceiver64 titanSimpleChannelReceiver64;

    public TitanSimpleChannelReceiverEntity64(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, SimpleVolume simpleVolume) {
        super(volumeManager, receiveStorageObject, destDirPath);
        this.channel = channel;
        this.simpleVolume = simpleVolume;
        this.titanSimpleChannelReceiver64 = new TitanSimpleChannelReceiver64( this );
    }


    @Override
    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    @Override
    public SimpleVolume getSimpleVolume() {
        return this.simpleVolume;
    }


    @Override
    public MiddleStorageObject receive() throws IOException, SQLException {
        return this.titanSimpleChannelReceiver64.channelReceive();
    }

    @Override
    public MiddleStorageObject receive(Number offset, Number endSize) throws IOException {
        return this.titanSimpleChannelReceiver64.channelReceive( offset, endSize );
    }
}
