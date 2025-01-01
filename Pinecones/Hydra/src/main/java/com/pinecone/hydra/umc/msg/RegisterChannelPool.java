package com.pinecone.hydra.umc.msg;

public interface RegisterChannelPool extends MappedChannelPool {
    long getMajorWaitTimeout();

    RegisterChannelPool setMajorWaitTimeout( long nMillisTimeout );

    int getMaximumPoolSize();
}
