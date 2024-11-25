package com.pinecone.hydra.storage.volume.entity.local.spanned.receive.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSpannedChannelReceiveEntity64 extends ArchReceiveEntity implements SpannedChannelReceiveEntity64{
    private KChannel                channel;
    private SpannedVolume           spannedVolume;
    private SpannedChannelReceive64 spannedChannelReceive64;


    public TitanSpannedChannelReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel, SpannedVolume spannedVolume) {
        super(volumeManager, storageReceiveIORequest);
        this.channel = channel;
        this.spannedVolume = spannedVolume;
        this.spannedChannelReceive64 = new TitanSpannedChannelReceive64( this );
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
    public SpannedVolume getSpannedVolume() {
        return this.spannedVolume;
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.spannedChannelReceive64.receive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.spannedChannelReceive64.receive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
    }
}
