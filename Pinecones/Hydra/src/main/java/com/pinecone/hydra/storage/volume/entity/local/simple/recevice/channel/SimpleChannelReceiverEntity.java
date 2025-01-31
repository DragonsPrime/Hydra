package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.channel;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleReceiverEntity;

public interface SimpleChannelReceiverEntity extends SimpleReceiverEntity {
    Chanface getChannel();
    void setChannel( Chanface channel );

    SimpleVolume getSimpleVolume();
}
