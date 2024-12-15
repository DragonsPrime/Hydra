package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.Map;

public interface ChannelPool extends Pinenut {
    ChannelControlBlock queryChannelById( Object id ) ;

    void onlyRemove( Object id );

    int size();

    void clear();

    boolean isEmpty();

    Map getPooledMap();

    ChannelControlBlock terminate( Object id ) throws InterruptedException;

    boolean isAllChannelsTerminated();

    void deactivate ( ChannelControlBlock ccb );

    ChannelPool setIdleChannel( ChannelControlBlock block );

    ChannelPool add( ChannelControlBlock block );

    ChannelControlBlock depriveIdleChannel();
}
