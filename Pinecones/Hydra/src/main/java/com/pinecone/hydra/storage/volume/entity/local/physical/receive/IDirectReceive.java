package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.stream.DirectStreamReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface IDirectReceive extends Receiver {
    StorageIOResponse receive() throws IOException;
    StorageIOResponse receive(Number offset, Number endSize) throws IOException;

    StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer ) throws IOException;
}
