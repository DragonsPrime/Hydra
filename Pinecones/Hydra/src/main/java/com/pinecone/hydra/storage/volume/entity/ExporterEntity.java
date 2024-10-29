package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.volume.VolumeTree;

public interface ExporterEntity extends Pinenut {
    VolumeTree getVolumeTree();
    void setVolumeTree( VolumeTree volumeTree );

   ExportStorageObject getExportStorageObject();
   void setExportStorageObject( ExportStorageObject exportStorageObject );
}
