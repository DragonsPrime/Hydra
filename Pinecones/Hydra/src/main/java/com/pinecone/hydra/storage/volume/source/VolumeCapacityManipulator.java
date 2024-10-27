package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;

public interface VolumeCapacityManipulator extends Pinenut {
    void insert( VolumeCapacity64 volumeCapacity );
    void remove( GUID guid );
    VolumeCapacity64 getVolumeCapacity(GUID guid);
}
