package com.pinecone.hydra.storage.volume.entity.local.physical.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.DirectReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel.DirectChannelReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectStreamReceive extends Receiver {
    StorageIOResponse receive(DirectStreamReceiveEntity entity ) throws IOException;
    StorageIOResponse receive(DirectStreamReceiveEntity entity, Number offset, Number endSize) throws IOException;
    StorageIOResponse receive(DirectStreamReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer ) throws IOException;
}
