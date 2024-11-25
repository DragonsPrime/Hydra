package com.pinecone.hydra.storage.volume.entity.local.striped.receive.channnel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanStripedChannelReceiverEntity64 extends ArchReceiveEntity implements StripedChannelReceiverEntity64{
    private KChannel                 channel;
    private StripedVolume            stripedVolume;
    private StripedChannelReceiver64 stripedChannelReceiver64;
    public TitanStripedChannelReceiverEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel, StripedVolume stripedVolume) {
        super(volumeManager, storageReceiveIORequest);
        this.channel = channel;
        this.stripedVolume = stripedVolume;
        this.stripedChannelReceiver64 = new TitanStripedChannelReceiver64( this );
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
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.stripedChannelReceiver64.channelReceive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.stripedChannelReceiver64.channelReceive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
    }
}
