package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.VolumeManager;

public interface ReceiveEntity extends Pinenut {
    VolumeManager getVolumeManager();
    void setVolumeManager(VolumeManager volumeManager);

    ReceiveStorageObject getReceiveStorageObject();
    void setReceiveStorageObject( ReceiveStorageObject receiveStorageObject );

    String getDestDirPath();
    void setDestDirPath( String destDirPath );

}
