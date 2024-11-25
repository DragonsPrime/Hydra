package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.SimpleReceiverEntity;

import java.nio.channels.FileChannel;

public interface SimpleChannelReceiverEntity extends SimpleReceiverEntity {
    KChannel getChannel();
    void setChannel( KChannel channel );

    SimpleVolume getSimpleVolume();
}
