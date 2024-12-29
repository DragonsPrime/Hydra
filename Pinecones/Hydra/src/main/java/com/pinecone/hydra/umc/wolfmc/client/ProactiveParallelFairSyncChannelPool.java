package com.pinecone.hydra.umc.wolfmc.client;

import java.util.concurrent.locks.Lock;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.SyncFairChannelPool;
import com.pinecone.hydra.umc.wolfmc.UlfIOLoadBalanceStrategy;

public class ProactiveParallelFairSyncChannelPool<ID > extends ProactiveParallelFairChannelPool<ID > implements SyncFairChannelPool {
    protected Lock mSynRequestLock;

    public ProactiveParallelFairSyncChannelPool( Lock synRequestLock, UlfIOLoadBalanceStrategy strategy ) {
        super(strategy);
        this.mSynRequestLock            = synRequestLock;
    }

    @Override
    public ChannelControlBlock nextSyncChannel( long nMillisTimeout, boolean bEager ) {
        this.mSynRequestLock.lock();

        if( this.mExclusiveSyncChannelCB != null ) {
            return this.mExclusiveSyncChannelCB;
        }
        else {
            ChannelControlBlock cb = null;
            try{
                cb = this.queryNextChannel( nMillisTimeout, bEager, true );
            }
            finally {
                this.mSynRequestLock.unlock();
            }
            return cb;
        }
    }

    @Override
    public ChannelControlBlock nextSyncChannel( long nMillisTimeout ) {
        return this.nextSyncChannel( nMillisTimeout, true );
    }

    @Override
    public ChannelControlBlock nextSyncChannel() {
        return this.nextSyncChannel( this.mnMajorWaitTimeout );
    }
}
