package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;

public interface ExporterEntity extends Pinenut {
    VolumeManager getVolumeManager();
    void setVolumeManager(VolumeManager volumeManager);

   StorageExportIORequest getStorageIORequest();
   void setStorageIORequest(StorageExportIORequest storageExportIORequest);
}
