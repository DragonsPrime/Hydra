package com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.DirectReceive;

import java.io.IOException;

public interface DirectChannelReceive extends DirectReceive {
    MiddleStorageObject receive(DirectChannelReceiveEntity entity ) throws IOException;
    MiddleStorageObject receive( DirectChannelReceiveEntity entity, Number offset, Number endSize) throws IOException;
}
