package com.pinecone.hydra.storage.volume.entity.local.striped.receive.channnel;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanStripedChannelReceiverEntity64 extends ArchReceiveEntity implements StripedChannelReceiverEntity64{
    private Chanface channel;
    private StripedVolume            stripedVolume;
    private StripedChannelReceiver64 stripedChannelReceiver64;
    public TitanStripedChannelReceiverEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, Chanface channel, StripedVolume stripedVolume) {
        super(volumeManager, storageReceiveIORequest,null);
        this.channel = channel;
        this.stripedVolume = stripedVolume;
        this.stripedChannelReceiver64 = new TitanStripedChannelReceiver64( this );
    }

    @Override
    public Chanface getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Chanface channel) {
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
