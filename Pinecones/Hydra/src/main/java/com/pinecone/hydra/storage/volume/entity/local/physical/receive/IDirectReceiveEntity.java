package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;

public interface IDirectReceiveEntity extends ReceiveEntity {
    String getDestDirPath();
    void setDestDirPath( String destDirPath );
}
