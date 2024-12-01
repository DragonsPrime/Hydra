package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.StorageConstants;
import com.pinecone.hydra.system.ko.ArchKernelObjectConfig;

public class KernelVolumeConfig extends ArchKernelObjectConfig implements VolumeConfig {
    protected String mszVersionSignature             = StorageConstants.StorageVersionSignature;

    protected Number mnTinyFileStripSizing           = VolumeConstants.TinyFileStripSizing  ;
    protected Number mnSmallFileStripSizing          = VolumeConstants.SmallFileStripSizing ;
    protected Number mnMegaFileStripSizing           = VolumeConstants.MegaFileStripSizing  ;
    protected Number mnDefaultStripSize              = VolumeConstants.DefaultStripSize     ;
    protected int    mStripResidentCacheAllotRatio   = VolumeConstants.StripResidentCacheAllotRatio;
    protected String mStorageObjectExtension         = VolumeConstants.StorageObjectExtension;
    protected String mSqliteFileExtension            = VolumeConstants.SqliteFileExtension;
    protected String mPathSeparator                  = VolumeConstants.PathSeparator;


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
    public int getStripResidentCacheAllotRatio() {
        return this.mStripResidentCacheAllotRatio;
    }

    @Override
    public String getStorageObjectExtension() {
        return this.mStorageObjectExtension;
    }

    @Override
    public String getSqliteFileExtension() {
        return this.mSqliteFileExtension;
    }

    @Override
    public String getPathSeparator() {
        return this.mPathSeparator;
    }
}
