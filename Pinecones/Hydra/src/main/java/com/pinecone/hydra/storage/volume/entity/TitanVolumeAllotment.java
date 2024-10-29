package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeTree;
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
    private VolumeTree              volumeTree;
    private VolumeMasterManipulator masterManipulator;

    public TitanVolumeAllotment( VolumeTree volumeTree, VolumeMasterManipulator volumeMasterManipulator ){
        this.volumeTree = volumeTree;
        this.masterManipulator= volumeMasterManipulator;
    }
    @Override
    public VolumeCapacity64 newVolumeCapacity() {
        return new TitanVolumeCapacity64( this.volumeTree,this.masterManipulator.getVolumeCapacityManipulator() );
    }

    @Override
    public LocalStripedVolume newLocalStripedVolume() {
        return new TitanLocalStripedVolume( this.volumeTree, this.masterManipulator.getStripedVolumeManipulator() );
    }

    @Override
    public LocalSpannedVolume newLocalSpannedVolume() {
        return new TitanLocalSpannedVolume( this.volumeTree, this.masterManipulator.getSpannedVolumeManipulator() );
    }

    @Override
    public LocalSimpleVolume newLocalSimpleVolume() {
        return new TitanLocalSimpleVolume( this.volumeTree, this.masterManipulator.getSimpleVolumeManipulator() );
    }

    @Override
    public LocalPhysicalVolume newLocalPhysicalVolume() {
        return new TitanLocalPhysicalVolume( this.volumeTree, this.masterManipulator.getPhysicalVolumeManipulator() );
    }

    @Override
    public MountPoint newMountPoint() {
        return new TitanMountPoint( this.volumeTree, this.masterManipulator.getMountPointManipulator() );
    }
}
