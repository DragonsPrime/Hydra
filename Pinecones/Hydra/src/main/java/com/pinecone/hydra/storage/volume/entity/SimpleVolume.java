package com.pinecone.hydra.storage.volume.entity;

public interface SimpleVolume extends LogicVolume{
    @Override
    default SimpleVolume evinceSimpleVolume() {
        return this;
    }
}
