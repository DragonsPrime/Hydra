package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface VolumeAllocateManipulator extends Pinenut {
    void insert( GUID objectGuid, GUID childVolumeGuid, GUID parentVolumeGuid );
    GUID get( GUID objectGuid, GUID parentGuid );
}
