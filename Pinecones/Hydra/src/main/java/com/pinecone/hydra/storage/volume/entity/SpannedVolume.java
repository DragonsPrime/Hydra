package com.pinecone.hydra.storage.volume.entity;


public interface SpannedVolume extends LogicVolume{
    @Override
    default SpannedVolume evinceSpannedVolume() {
        return this;
    }
}
