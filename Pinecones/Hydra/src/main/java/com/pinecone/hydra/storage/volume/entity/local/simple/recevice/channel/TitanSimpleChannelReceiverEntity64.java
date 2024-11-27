package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSimpleChannelReceiverEntity64 extends ArchReceiveEntity implements SimpleChannelReceiverEntity64{

    private KChannel                channel;
    private SimpleVolume            simpleVolume;
    private SimpleChannelReceiver64 titanSimpleChannelReceiver64;

    public TitanSimpleChannelReceiverEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel, SimpleVolume simpleVolume) {
        super(volumeManager, storageReceiveIORequest);
        this.channel = channel;
        this.simpleVolume = simpleVolume;
        this.titanSimpleChannelReceiver64 = new TitanSimpleChannelReceiver64( this );
    }


    @Override
    public KChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(KChannel channel) {
        this.channel = channel;
    }

    @Override
    public SimpleVolume getSimpleVolume() {
        return this.simpleVolume;
    }


    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.titanSimpleChannelReceiver64.channelReceive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException {
        return this.titanSimpleChannelReceiver64.channelReceive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
    }
}
