package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.VolumeManager;

public interface ExporterEntity extends Pinenut {
    VolumeManager getVolumeManager();
    void setVolumeManager(VolumeManager volumeManager);

   ExportStorageObject getExportStorageObject();
   void setExportStorageObject( ExportStorageObject exportStorageObject );
}
