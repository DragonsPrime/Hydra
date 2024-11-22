package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

import java.nio.channels.FileChannel;

public interface StripedChannelReceiverEntity extends StripedReceiverEntity{
    KChannel getChannel();
    void setChannel( KChannel channel );
    StripedVolume getStripedVolume();
}
