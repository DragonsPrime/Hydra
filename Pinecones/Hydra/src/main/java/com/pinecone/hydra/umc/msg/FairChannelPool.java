package com.pinecone.hydra.umc.msg;

import java.util.Queue;

public interface FairChannelPool extends AsynChannelAllocator {
    long getMajorWaitTimeout();

    FairChannelPool setMajorWaitTimeout( long nMillisTimeout );


    FairChannelPool pushBack( ChannelControlBlock channel );

    ChannelControlBlock pop();

    @Override
    FairChannelPool setIdleChannel( ChannelControlBlock block );

    @Override
    ChannelControlBlock nextAsynChannel( long nMillisTimeout, boolean bEager ) ;

    @Override
    default ChannelControlBlock nextAsynChannel( long nMillisTimeout ) {
        return this.nextAsynChannel( nMillisTimeout, true );
    }

    @Override
    default ChannelControlBlock nextAsynChannel() {
        return this.nextAsynChannel( 5000 );
    }

    Queue getMajorQueue();
}
