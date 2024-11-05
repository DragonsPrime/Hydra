package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.nio.channels.FileChannel;

public interface StripedChannelReceiverEntity extends StripedReceiverEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
    StripedVolume getStripedVolume();
}
