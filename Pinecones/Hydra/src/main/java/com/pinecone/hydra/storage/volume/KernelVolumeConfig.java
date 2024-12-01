package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.StorageConstants;
import com.pinecone.hydra.system.ko.ArchKernelObjectConfig;

public class KernelVolumeConfig extends ArchKernelObjectConfig implements VolumeConfig {
    protected String mszVersionSignature    = StorageConstants.StorageVersionSignature;

    protected Number mnTinyFileStripSizing  = VolumeConstants.TinyFileStripSizing  ;
    protected Number mnSmallFileStripSizing = VolumeConstants.SmallFileStripSizing ;
    protected Number mnMegaFileStripSizing  = VolumeConstants.MegaFileStripSizing  ;
    protected Number mnDefaultStripSize     = VolumeConstants.DefaultStripSize     ;
    protected int    StripResidentCacheAllotRatio   = VolumeConstants.StripResidentCacheAllotRatio;
    protected String storageObjectExtension = VolumeConstants.StorageObjectExtension;
    protected String sqliteFileExtension    = VolumeConstants.SqliteFileExtension;
    protected String pathSeparator          = VolumeConstants.PathSeparator;


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
        return this.StripResidentCacheAllotRatio;
    }

    @Override
    public String getStorageObjectExtension() {
        return this.storageObjectExtension;
    }

    @Override
    public String getSqliteFileExtension() {
        return this.sqliteFileExtension;
    }

    @Override
    public String getPathSeparator() {
        return this.pathSeparator;
    }
}
