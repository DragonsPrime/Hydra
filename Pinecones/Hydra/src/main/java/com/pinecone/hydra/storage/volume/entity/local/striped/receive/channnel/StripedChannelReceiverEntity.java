package com.pinecone.hydra.storage.volume.entity.local.striped.receive.channnel;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.StripedReceiverEntity;

public interface StripedChannelReceiverEntity extends StripedReceiverEntity {
    Chanface getChannel();
    void setChannel( Chanface channel );
    StripedVolume getStripedVolume();
}
