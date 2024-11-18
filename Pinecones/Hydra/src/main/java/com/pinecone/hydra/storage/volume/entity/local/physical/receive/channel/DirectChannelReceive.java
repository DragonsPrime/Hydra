package com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.DirectReceive;

import java.io.IOException;

public interface DirectChannelReceive extends DirectReceive {
    StorageIOResponse receive(DirectChannelReceiveEntity entity ) throws IOException;
    StorageIOResponse receive(DirectChannelReceiveEntity entity, Number offset, Number endSize) throws IOException;
}
