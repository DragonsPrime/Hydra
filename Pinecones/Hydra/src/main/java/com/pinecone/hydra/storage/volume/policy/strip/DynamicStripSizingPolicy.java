package com.pinecone.hydra.storage.volume.policy.strip;

import java.util.List;

import com.pinecone.framework.system.prototype.Pinenut;

public interface DynamicStripSizingPolicy extends Pinenut {
    Number evalStripSize( Number integritySize );

    List<SizingMatcher> getMatchers();

    int getLevelByMatcher( SizingMatcher that );

    Number getDefaultStripSize();
}
