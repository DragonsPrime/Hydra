package com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.DirectReceiveEntity;

import java.nio.channels.FileChannel;

public interface DirectChannelReceiveEntity extends DirectReceiveEntity {
    KChannel getChannel();
    void setChannel( KChannel channel );

    String getDestDirPath();
    void setDestDirPath( String destDirPath );
}
