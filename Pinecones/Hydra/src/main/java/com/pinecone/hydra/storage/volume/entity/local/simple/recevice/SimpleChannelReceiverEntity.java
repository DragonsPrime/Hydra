package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

import java.nio.channels.FileChannel;

public interface SimpleChannelReceiverEntity extends SimpleReceiverEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );

    SimpleVolume getSimpleVolume();
}
