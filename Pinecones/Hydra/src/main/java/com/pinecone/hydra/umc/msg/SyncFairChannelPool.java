package com.pinecone.hydra.umc.msg;

public interface SyncFairChannelPool extends FairChannelPool {
    ChannelControlBlock nextSyncChannel( long nMillisTimeout, boolean bEager ) ;

    default ChannelControlBlock nextSyncChannel( long nMillisTimeout ) {
        return this.nextSyncChannel( nMillisTimeout, true );
    }

    default ChannelControlBlock nextSyncChannel() {
        return this.nextSyncChannel( 5000 );
    }
}
