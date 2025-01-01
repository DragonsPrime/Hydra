package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

public interface SimpleReceiveEntity extends ReceiveEntity {
    SimpleVolume getSimpleVolume();
}
