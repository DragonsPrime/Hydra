package com.pinecone.hydra.storage.volume.policy.strip;

import com.pinecone.framework.system.prototype.Pinenut;

public interface SizingMatcher extends Pinenut {
    Number isMatched( Number integritySize );

    Number getLevelSize();

    int getLevel();

    DynamicStripSizingPolicy getSizingPolicy();
}
