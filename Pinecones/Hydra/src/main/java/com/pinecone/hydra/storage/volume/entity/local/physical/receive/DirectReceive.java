package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel.DirectChannelReceiveEntity;

import java.io.IOException;

public interface DirectReceive extends Receiver {
    StorageIOResponse receive(DirectChannelReceiveEntity entity ) throws IOException;
    StorageIOResponse receive(DirectChannelReceiveEntity entity, Number offset, Number endSize) throws IOException;
}
