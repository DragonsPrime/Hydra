package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class TitanStripedChannelReceiverEntity64 extends ArchReceiveEntity implements StripedChannelReceiverEntity64{
    private FileChannel channel;
    private StripedVolume stripedVolume;
    private StripedChannelReceiver64 stripedChannelReceiver64;
    public TitanStripedChannelReceiverEntity64(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, FileChannel channel, StripedVolume stripedVolume) {
        super(volumeManager, receiveStorageObject);
        this.channel = channel;
        this.stripedVolume = stripedVolume;
        this.stripedChannelReceiver64 = new TitanStripedChannelReceiver64( this );
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
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }

    @Override
    public MiddleStorageObject receive() throws IOException, SQLException {
        return this.stripedChannelReceiver64.channelReceive();
    }

    @Override
    public MiddleStorageObject receive(Number offset, Number endSize) throws IOException {
        return null;
    }
}
