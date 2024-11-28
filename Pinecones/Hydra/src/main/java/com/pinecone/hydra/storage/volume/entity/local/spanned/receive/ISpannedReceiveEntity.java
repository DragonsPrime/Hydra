package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;

public interface ISpannedReceiveEntity extends ReceiveEntity {
    SpannedVolume  getSpannedVolume();
}
