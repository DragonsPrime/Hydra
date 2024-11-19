package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;

public interface ReceiveEntity extends Pinenut {
    VolumeManager getVolumeManager();
    void setVolumeManager(VolumeManager volumeManager);

    StorageReceiveIORequest getReceiveStorageObject();
    void setReceiveStorageObject( StorageReceiveIORequest storageReceiveIORequest);


}
