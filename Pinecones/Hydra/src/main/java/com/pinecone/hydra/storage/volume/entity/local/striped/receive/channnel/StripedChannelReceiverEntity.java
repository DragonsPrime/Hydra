package com.pinecone.hydra.storage.volume.entity.local.striped.receive.channnel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.StripedReceiverEntity;

import java.nio.channels.FileChannel;

public interface StripedChannelReceiverEntity extends StripedReceiverEntity {
    KChannel getChannel();
    void setChannel( KChannel channel );
    StripedVolume getStripedVolume();
}
