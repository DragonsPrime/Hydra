package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.UniformFile;

public interface VolumeFile extends UniformFile {
    VolumeFile fromUniformFile( UniformFile file );
}


