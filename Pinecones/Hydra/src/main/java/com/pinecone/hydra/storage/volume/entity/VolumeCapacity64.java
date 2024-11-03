package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.entity.local.VolumeCapacity;

public interface VolumeCapacity64 extends VolumeCapacity {
    @Override
    Long getDefinitionCapacity();

    @Override
    void setDefinitionCapacity( Number definitionCapacity );

    @Override
    Long getUsedSize();

    @Override
    void setUsedSize( Number usedSize );

    @Override
    Long getQuotaCapacity();

    @Override
    void setQuotaCapacity( Number quotaCapacity );
}
