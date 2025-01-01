package com.pinecone.hydra.umc.msg;

public interface AsynChannelAllocator extends ChannelPool {
    ChannelControlBlock nextAsynChannel( long nMillisTimeout, boolean bEager ) ;

    default ChannelControlBlock nextAsynChannel( long nMillisTimeout ) {
        return this.nextAsynChannel( nMillisTimeout, true );
    }

    default ChannelControlBlock nextAsynChannel() {
        return this.nextAsynChannel( 5000 );
    }
}
