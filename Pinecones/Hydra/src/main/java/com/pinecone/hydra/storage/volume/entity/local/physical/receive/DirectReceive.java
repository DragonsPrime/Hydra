package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.entity.Receiver;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;

public interface DirectReceive extends Receiver {
    StorageIOResponse receive(Chanface chanface,CacheBlock cacheBlock, byte[] buffer ) throws IOException;
}
