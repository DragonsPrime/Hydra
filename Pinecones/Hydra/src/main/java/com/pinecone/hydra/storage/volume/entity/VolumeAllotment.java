package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;

public interface VolumeAllotment extends Pinenut {
    VolumeCapacity64 newVolumeCapacity();
    LocalStripedVolume      newLocalStripedVolume();
    LocalSpannedVolume      newLocalSpannedVolume();
    LocalSimpleVolume       newLocalSimpleVolume();
    LocalPhysicalVolume     newLocalPhysicalVolume();
    MountPoint              newMountPoint();
}
