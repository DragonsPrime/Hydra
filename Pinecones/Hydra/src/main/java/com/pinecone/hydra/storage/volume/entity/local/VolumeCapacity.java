package com.pinecone.hydra.storage.volume.entity.local;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface VolumeCapacity extends Pinenut {
    GUID getVolumeGuid();

    void setVolumeGuid( GUID volumeGuid );

    Number getDefinitionCapacity();

    void setDefinitionCapacity( Number definitionCapacity );

    Number getUsedSize();

    void setUsedSize( Number usedSize );

    Number getQuotaCapacity();

    void setQuotaCapacity( Number quotaCapacity );
}
