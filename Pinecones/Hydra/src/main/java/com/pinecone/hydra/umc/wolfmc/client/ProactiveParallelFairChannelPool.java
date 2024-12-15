package com.pinecone.hydra.umc.wolfmc.client;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.msg.FairChannelPool;
import com.pinecone.hydra.umc.msg.UMCChannel;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.hydra.umc.wolfmc.ArchChannelPool;
import com.pinecone.hydra.umc.wolfmc.UlfChannelStatus;
import com.pinecone.hydra.umc.wolfmc.UlfIOLoadBalanceStrategy;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProactiveParallelFairChannelPool<ID > extends ArchChannelPool implements FairChannelPool {
    protected ReentrantReadWriteLock   mPoolIOLock = new ReentrantReadWriteLock();

    protected ChannelControlBlock      mExclusiveSyncChannelCB; // The exclusive channel only for synchronized messages only.

    protected UlfIOLoadBalanceStrategy mLoadBalanceStrategy;

    protected ArchAsyncMessenger       mMessenger;

    protected long                     mnMajorWaitTimeout = 5000;

    protected LinkedTreeMap<ID, ChannelControlBlock >  mChannelMapQueue;

    protected LinkedTreeMap<ID, ChannelControlBlock >  mChannelIdleQueue;


    public ProactiveParallelFairChannelPool( ArchAsyncMessenger messenger, UlfIOLoadBalanceStrategy strategy ) {
        this.mMessenger            = messenger;
        this.mLoadBalanceStrategy  = strategy;
        this.mChannelMapQueue      = new LinkedTreeMap<>();
        this.mChannelIdleQueue     = new LinkedTreeMap<>();
    }


    public ProactiveParallelFairChannelPool setExclusiveSyncChannel( ChannelControlBlock exclusiveSyncChannelCB ) {
        this.mExclusiveSyncChannelCB = exclusiveSyncChannelCB;
        return this;
    }

    public UMCChannel getExclusiveSyncChannel() {
        if( this.mExclusiveSyncChannelCB != null ) {
            return this.mExclusiveSyncChannelCB.getChannel();
        }
        return null;
    }

    @SuppressWarnings( "unchecked" )
    protected ID warpKey( Object id ) {
        return (ID)id;
    }

    @Override
    public ChannelControlBlock queryChannelById( Object id ) {
        return this.mChannelMapQueue.get( this.warpKey( id ) );
    }

    @Override
    public void onlyRemove( Object id ) {
        this.mChannelMapQueue.remove ( this.warpKey( id ) );
        this.mChannelIdleQueue.remove( this.warpKey( id ) );
    }

    public ArchAsyncMessenger getMessenger() {
        return this.mMessenger;
    }

    @Override
    public long getMajorWaitTimeout() {
        return this.mnMajorWaitTimeout;
    }

    @Override
    public ProactiveParallelFairChannelPool setMajorWaitTimeout( long nMillisTimeout ){
        this.mnMajorWaitTimeout = nMillisTimeout;
        return this;
    }


    // [1, 2] -> [1, 2, 3]
    public ProactiveParallelFairChannelPool pushBack( ChannelControlBlock channel ) {
        ID id = this.warpKey( channel.getChannel().getChannelID() );
        this.mChannelMapQueue.put( id, channel );
        this.mChannelIdleQueue.put( id, channel );
        return this;
    }

    // [1, 2, 3] ->[2, 3]
    public ChannelControlBlock pop() {
        return this.mChannelMapQueue.pop().getValue();
    }

    @Override
    public ChannelControlBlock depriveIdleChannel() {
        this.mPoolIOLock.writeLock().lock();
        try{
            ChannelControlBlock qualified = null;
            for ( Map.Entry<ID, ChannelControlBlock> kv : this.mChannelMapQueue.entrySet() ) {
                ChannelControlBlock block = kv.getValue();
                if( block.getChannelStatus().isIdle() )  {
                    qualified = block;
                    break;
                }
            }

            if ( qualified != null ) {
                this.onlyRemove( qualified.getChannel().getChannelID() );
            }

            return qualified;
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
    }

    @Override
    public ProactiveParallelFairChannelPool setIdleChannel( ChannelControlBlock block ) {
        this.mPoolIOLock.writeLock().lock();
        try{
            block.getChannel().setChannelStatus( UlfChannelStatus.IDLE );
            this.mChannelIdleQueue.put( this.warpKey( block.getChannel().getChannelID() ), block );
            //Debug.trace( this.mChannelIdleQueue, this.mChannelIdleQueue.size(), block );
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
        return this;
    }

    @Override
    public ChannelPool add( ChannelControlBlock block ) {
        this.mPoolIOLock.writeLock().lock();
        try{
            this.pushBack( block );
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
        return this;
    }

    protected ChannelControlBlock queryNextChannel(long nMillisTimeout, boolean bEager, boolean bSync ) {
        ChannelControlBlock nextChannel     = null;

        if( this.mChannelMapQueue.isEmpty() ) {
            return null;
        }

        long nLastTime = System.currentTimeMillis();
        while ( true ) {
            this.mPoolIOLock.readLock().lock();
            boolean bReadLocked = true;

            try{
                if( !this.mChannelIdleQueue.isEmpty() ) {
                    this.mPoolIOLock.readLock().unlock();
                    bReadLocked = false;

                    // Condition1: If there has an idle, just use it.
                    this.mPoolIOLock.writeLock().lock();
                    try{
                        nextChannel = this.mChannelIdleQueue.pop().getValue();
                    }
                    finally {
                        this.mPoolIOLock.writeLock().unlock();
                    }
                }
                else {
                    // Condition2: If there are no idles, waiting and found balance channel.
                    // Notice: In asynchronous condition, the producer could produce over-allocated messages and dump them into the queue of one channel so that consumers will mismatch the produced messages.
                    // Using LinkedTreeMapQueue to sift repetitive idle channel and keep the queue.
                    try{
                        if( bSync ) {
                            for ( Map.Entry<ID, ChannelControlBlock> kv : this.mChannelMapQueue.entrySet() ) {
                                ChannelControlBlock block = kv.getValue();
                                if( this.mLoadBalanceStrategy.apply( block ).matched() || block.isShutdown() ) {
                                    nextChannel = block;
                                    break;
                                }
                            }
                        }
                        else {
                            for ( Map.Entry<ID, ChannelControlBlock> kv : this.mChannelMapQueue.entrySet() ) {
                                ChannelControlBlock block = kv.getValue();
                                boolean bFirstStrategyMatched = this.mLoadBalanceStrategy.apply( block ).matched();
                                if( bFirstStrategyMatched || block.getChannelStatus().isAsynAvailable() || block.isShutdown() )  {
                                    nextChannel = block;
                                    break;
                                }
                            }
                        }
                    }
                    finally {
                        this.mPoolIOLock.readLock().unlock();
                    }
                }
            }
            finally {
                if ( bReadLocked || this.mPoolIOLock.getReadLockCount() > 0 ) {
                    this.mPoolIOLock.readLock().unlock();
                }
            }

            if( nextChannel != null ) {
                this.mPoolIOLock.writeLock().lock();
                try{
                    ID id = this.warpKey( nextChannel.getChannel().getChannelID() );
                    this.mChannelMapQueue.remove( id );
                    this.mChannelMapQueue.put( id, nextChannel ); // push back to queue tail
                }
                finally {
                    this.mPoolIOLock.writeLock().unlock();
                }
                break;
            }

            if( !bEager ) {
                try{
                    Thread.sleep( 10 );
                }
                catch ( InterruptedException e ) {
                    break;
                }
            }

            if( nMillisTimeout > 0 && System.currentTimeMillis() - nLastTime > nMillisTimeout ) {
                break;
            }
        }
        return nextChannel;
    }

    @Override
    public ChannelControlBlock nextSyncChannel( long nMillisTimeout, boolean bEager ) {
        this.getMessenger().getSynRequestLock().lock();

        if( this.mExclusiveSyncChannelCB != null ) {
            return this.mExclusiveSyncChannelCB;
        }
        else {
            ChannelControlBlock cb = null;
            try{
                cb = this.queryNextChannel( nMillisTimeout, bEager, true );
            }
            finally {
                this.getMessenger().getSynRequestLock().unlock();
            }
            return cb;
        }
    }

    @Override
    public ChannelControlBlock nextAsynChannel( long nMillisTimeout, boolean bEager ) {
        return this.queryNextChannel( nMillisTimeout, bEager, false );
    }

    @Override
    public ChannelControlBlock nextSyncChannel( long nMillisTimeout ) {
        return this.nextSyncChannel( nMillisTimeout, true );
    }

    @Override
    public ChannelControlBlock nextAsynChannel( long nMillisTimeout ) {
        return this.nextAsynChannel( nMillisTimeout, true );
    }

    @Override
    public ChannelControlBlock nextSyncChannel() {
        return this.nextSyncChannel( this.mnMajorWaitTimeout );
    }

    @Override
    public ChannelControlBlock nextAsynChannel() {
        return this.nextAsynChannel( this.mnMajorWaitTimeout );
    }

    @Override
    public boolean isEmpty() {
        return this.mChannelMapQueue.isEmpty();
    }

    @Override
    public int size() {
        return this.mChannelMapQueue.size();
    }

    @Override
    public void clear() {
        this.mPoolIOLock.writeLock().lock();
        try{
            for( ChannelControlBlock block : this.mChannelMapQueue.values() ) {
                block.close();
                block.release();
            }

            this.mChannelMapQueue.clear();
            this.mChannelIdleQueue.clear();
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
    }

    @Override
    public Map getPooledMap() {
        return this.mChannelMapQueue;
    }

    @Override
    public Queue getMajorQueue() {
        return this.mChannelMapQueue.toQueue();
    }

    @Override
    public void deactivate( ChannelControlBlock ccb ) {
        this.mPoolIOLock.writeLock().lock();
        try {
            ID id = this.warpKey( ccb.getChannel().getChannelID() );
            if( !ccb.getChannel().isShutdown() ) {
                ccb.close();
                ccb.release();
                this.onlyRemove( id );
            }
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
    }

    @Override
    public ChannelControlBlock terminate( Object id ) throws InterruptedException {
        this.mPoolIOLock.writeLock().lock();
        ChannelControlBlock block = null;
        try{
            block = this.queryChannelById( id );
            if( block != null ) {
                block.close();
                block.release();
            }
            this.onlyRemove( id );
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
        return block;
    }

}
