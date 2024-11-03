package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.TitanLocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.TitanLocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.spanned.TitanLocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanLocalStripedVolume;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;

public class TitanVolumeAllotment implements VolumeAllotment{
    private VolumeManager volumeManager;
    private VolumeMasterManipulator masterManipulator;

    public TitanVolumeAllotment(VolumeManager volumeManager, VolumeMasterManipulator volumeMasterManipulator ){
        this.volumeManager = volumeManager;
        this.masterManipulator= volumeMasterManipulator;
    }
    @Override
    public VolumeCapacity64 newVolumeCapacity() {
        return new TitanVolumeCapacity64( this.volumeManager,this.masterManipulator.getVolumeCapacityManipulator() );
    }

    @Override
    public LocalStripedVolume newLocalStripedVolume() {
        return new TitanLocalStripedVolume( this.volumeManager, this.masterManipulator.getStripedVolumeManipulator() );
    }

    @Override
    public LocalSpannedVolume newLocalSpannedVolume() {
        return new TitanLocalSpannedVolume( this.volumeManager, this.masterManipulator.getSpannedVolumeManipulator() );
    }

    @Override
    public LocalSimpleVolume newLocalSimpleVolume() {
        return new TitanLocalSimpleVolume( this.volumeManager, this.masterManipulator.getSimpleVolumeManipulator() );
    }

    @Override
    public LocalPhysicalVolume newLocalPhysicalVolume() {
        return new TitanLocalPhysicalVolume( this.volumeManager, this.masterManipulator.getPhysicalVolumeManipulator() );
    }

    @Override
    public MountPoint newMountPoint() {
        return new TitanMountPoint( this.volumeManager, this.masterManipulator.getMountPointManipulator() );
    }
}
