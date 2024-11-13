package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.StorageConstants;
import com.pinecone.hydra.system.ko.ArchKernelObjectConfig;

public class KernelVolumeConfig extends ArchKernelObjectConfig implements VolumeConfig {
    protected String mszVersionSignature    = StorageConstants.StorageVersionSignature;

    protected Number mnTinyFileStripSizing  = VolumeConstants.TinyFileStripSizing  ;
    protected Number mnSmallFileStripSizing = VolumeConstants.SmallFileStripSizing ;
    protected Number mnMegaFileStripSizing  = VolumeConstants.MegaFileStripSizing  ;
    protected Number mnDefaultStripSize     = VolumeConstants.DefaultStripSize     ;
    protected int    superResolutionRatio   = VolumeConstants.superResolutionRatio;


    @Override
    public String getVersionSignature() {
        return this.mszVersionSignature;
    }


    @Override
    public Number getTinyFileStripSizing() {
        return this.mnTinyFileStripSizing;
    }

    @Override
    public Number getSmallFileStripSizing() {
        return this.mnSmallFileStripSizing;
    }

    @Override
    public Number getMegaFileStripSizing() {
        return this.mnMegaFileStripSizing;
    }

    @Override
    public Number getDefaultStripSize() {
        return this.mnDefaultStripSize;
    }

    @Override
    public int getSuperResolutionRatio() {
        return this.superResolutionRatio;
    }
}
