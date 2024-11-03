package com.pinecone.hydra.storage.volume.entity;

public interface StripedVolume extends LogicVolume{
    @Override
    default StripedVolume evinceStripeVolume() {
        return this;
    }
}
