package com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel;

import com.pinecone.hydra.storage.volume.entity.local.physical.receive.DirectReceiveEntity;

import java.nio.channels.FileChannel;

public interface DirectChannelReceiveEntity extends DirectReceiveEntity {
    FileChannel getChannel();
    void setChannel( FileChannel channel );
}
