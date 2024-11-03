package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import java.nio.channels.FileChannel;

public interface DirectChannelReceiveEntity extends DirectReceiveEntity{
    FileChannel getChannel();
    void setChannel( FileChannel channel );
}
