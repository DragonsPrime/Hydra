package com.pinecone.hydra.storage.volume.source;

import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public interface VolumeMasterManipulator extends KOIMasterManipulator {
    MirroredVolumeManipulator getMirroredVolumeManipulator();
    MountPointManipulator     getMountPointManipulator();
    SimpleVolumeManipulator    getSimpleVolumeManipulator();
    SpannedVolumeManipulator   getSpannedVolumeManipulator();
    StripedVolumeManipulator   getStripedVolumeManipulator();
    VolumeCapacityManipulator   getVolumeCapacityManipulator();
    PhysicalVolumeManipulator   getPhysicalVolumeManipulator();
    VolumeAllocateManipulator   getVolumeAllocateManipulator();
}
