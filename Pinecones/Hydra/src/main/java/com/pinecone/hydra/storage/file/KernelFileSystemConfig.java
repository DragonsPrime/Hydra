package com.pinecone.hydra.storage.file;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.StorageConstants;
import com.pinecone.hydra.storage.volume.VolumeConstants;
import com.pinecone.hydra.system.ko.ArchKernelObjectConfig;

public class KernelFileSystemConfig extends ArchKernelObjectConfig implements FileSystemConfig {
    protected String mszVersionSignature    = StorageConstants.StorageVersionSignature;
    protected Number mnFrameSize            = FileConstants.DefaultFrameSize;
    protected GUID   mLocalhostGUID         = StorageConstants.LocalhostGUID;
    protected Number TinyFileStripSizing    = VolumeConstants.TinyFileStripSizing;
    protected String DefaultVolumePath      = StorageConstants.DefaultVolumePath;

    @Override
    public String getVersionSignature() {
        return this.mszVersionSignature;
    }

    public Number getFrameSize() {
        return this.mnFrameSize;
    }

    public GUID getLocalhostGUID() {
        return this.mLocalhostGUID;
    }

    @Override
    public Number getTinyFileStripSizing() {
        return this.TinyFileStripSizing;
    }

    @Override
    public String getDefaultVolume() {
        return this.DefaultVolumePath;
    }
}
