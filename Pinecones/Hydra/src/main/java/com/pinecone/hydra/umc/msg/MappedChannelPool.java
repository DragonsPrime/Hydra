package com.pinecone.hydra.umc.msg;

import java.util.Collection;
import java.util.Map;

public interface MappedChannelPool extends ChannelPool {
    Map getPooledMap();

    @Override
    default Collection getPooledChannels() {
        return this.getPooledMap().values();
    }
}
