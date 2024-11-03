package com.pinecone.hydra.storage.volume.entity;

public interface MirroredVolume extends LogicVolume{
    @Override
    default MirroredVolume evinceMirroredVolume() {
        return this;
    }
}
