package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.MountPoint;

public interface MountPointManipulator extends Pinenut {
    void insert( MountPoint mountPoint );

    void remove( GUID guid );

    void removeByVolumeGuid( GUID guid );

    MountPoint getMountPoint(GUID guid);

    MountPoint getMountPointByVolumeGuid( GUID guid );

}
