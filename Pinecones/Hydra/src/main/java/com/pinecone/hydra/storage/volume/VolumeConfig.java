package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.system.ko.KernelObjectConfig;

public interface VolumeConfig extends KernelObjectConfig {
    String getVersionSignature();

    Number getTinyFileStripSizing() ;

    Number getSmallFileStripSizing() ;

    Number getMegaFileStripSizing() ;

    Number getDefaultStripSize() ;

    int getStripResidentCacheAllotRatio();
}
