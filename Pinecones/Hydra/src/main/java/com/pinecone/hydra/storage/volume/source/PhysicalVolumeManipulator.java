package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;

public interface PhysicalVolumeManipulator extends Pinenut {
    void insert( PhysicalVolume physicalVolume );
    void remove( GUID guid );
    PhysicalVolume getPhysicalVolume(GUID guid);
    PhysicalVolume getPhysicalVolumeByName( String name );
    PhysicalVolume getSmallestCapacityPhysicalVolume();
}
