package com.pinecone.hydra.storage.volume;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;

public interface VolumeManager extends KOMInstrument {
    VolumeConfig KernelVolumeConfig = new KernelVolumeConfig();

    @Override
    LogicVolume get( GUID guid );

    @Override
    VolumeConfig getConfig();

    PhysicalVolume getPhysicalVolume( GUID guid );

    GUID insertPhysicalVolume( PhysicalVolume physicalVolume );

    void purgePhysicalVolume( GUID guid );

    void insertAllocate( GUID objectGuid, GUID childVolumeGuid, GUID parentVolumeGuid );

    PhysicalVolume getSmallestCapacityPhysicalVolume();

    VolumeMasterManipulator  getMasterManipulator();
}
