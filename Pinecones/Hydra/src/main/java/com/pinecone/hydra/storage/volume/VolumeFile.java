package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.CheckedFile;

public interface VolumeFile extends CheckedFile {
    VolumeFile fromUniformFile( CheckedFile file );
}


