package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;

public interface IStripedReceiveEntity extends ReceiveEntity {
    StripedVolume  getStripedVolume();

}
