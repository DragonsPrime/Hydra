package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.hydra.storage.volume.runtime.VolumeJob;

public interface StripBufferWriteJob extends VolumeJob {
    BufferWriteStatus getStatus();
}
