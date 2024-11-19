package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanSpannedChannelReceiveEntity64 extends ArchReceiveEntity implements SpannedChannelReceiveEntity64{
    private FileChannel             channel;
    private SpannedVolume           spannedVolume;
    private SpannedChannelReceive64 spannedChannelReceive64;


    public TitanSpannedChannelReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, FileChannel channel, SpannedVolume spannedVolume) {
        super(volumeManager, storageReceiveIORequest);
        this.channel = channel;
        this.spannedVolume = spannedVolume;

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
    public SpannedVolume getSpannedVolume() {
        return this.spannedVolume;
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        TitanSpannedChannelReceive64 titanSpannedChannelReceive64 = new TitanSpannedChannelReceive64( this );
        return titanSpannedChannelReceive64.receive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        TitanSpannedChannelReceive64 titanSpannedChannelReceive64 = new TitanSpannedChannelReceive64( this );
        return titanSpannedChannelReceive64.receive( offset, endSize );
    }
}
