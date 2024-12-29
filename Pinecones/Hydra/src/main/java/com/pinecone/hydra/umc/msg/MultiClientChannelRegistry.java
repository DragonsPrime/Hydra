package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.Pinenut;

public interface MultiClientChannelRegistry<CID > extends Pinenut {
    int size();

    void clear();

    boolean isEmpty();

    void register( CID id, ChannelControlBlock controlBlock );
}
