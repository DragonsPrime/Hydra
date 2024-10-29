package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.VolumeFile;
import com.pinecone.hydra.storage.volume.VolumeTree;

public interface ReceiveEntity extends Pinenut {
    VolumeTree getVolumeTree();
    void setVolumeTree( VolumeTree volumeTree );

    ReceiveStorageObject getReceiveStorageObject();
    void setReceiveStorageObject( ReceiveStorageObject receiveStorageObject );

    String getDestDirPath();
    void setDestDirPath( String destDirPath );

}
