package com.pinecone.hydra.umc.wolfmc.server;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.msg.RegisterChannelPool;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.wolfmc.ArchChannelPool;
import com.pinecone.hydra.umc.wolfmc.InternalErrors;
import com.pinecone.hydra.umc.wolfmc.UlfIOLoadBalanceStrategy;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Description PassiveRegisterChannelPool
 * @Author DragonKing, welsir
 * @Date 2024/6/30 16.41
 */
public class PassiveRegisterChannelPool<ID > extends ArchChannelPool implements RegisterChannelPool {
    protected LinkedTreeMap<ID, ChannelControlBlock >    mChannelMapPool;
    protected UlfIOLoadBalanceStrategy                   mLoadBalanceStrategy;
    protected final int                                  mnMaximumPoolSize   ;
    protected long                                       mnMajorWaitTimeout = 5000;
    protected WolfMCServer                               mRecipient;
    protected ReentrantReadWriteLock                     mPoolIOLock = new ReentrantReadWriteLock();

    public PassiveRegisterChannelPool( WolfMCServer recipient, UlfIOLoadBalanceStrategy strategy, int nMaximumPoolSize ) {
        this.mRecipient            = recipient;
        this.mLoadBalanceStrategy  = strategy;
        this.mChannelMapPool       = new LinkedTreeMap<>();
        this.mnMaximumPoolSize     = nMaximumPoolSize;
    }

    protected ChannelControlBlock addChannel( ChannelControlBlock channel ){
        try {
            this.mPoolIOLock.writeLock().lock();
            if( this.size() >= this.mnMaximumPoolSize ){
                try{
                    InternalErrors.sendTooManyConnections( channel );
                    channel.close();
                }
                catch ( IOException e ) {
                    throw new ProxyProvokeHandleException( e );
                }
                return null;
            }
            ID channelId = this.warpKey( channel.getChannel().getChannelID() ) ;
            this.mChannelMapPool.put( channelId, channel );
            return channel;
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
    }

    @Override
    public ChannelControlBlock depriveIdleChannel() {
        throw new UnsupportedOperationException( "Method `depriveIdleChannel` is inapplicable for `PassiveRegisterChannelPool`." );
    }

    @SuppressWarnings( "unchecked" )
    protected ID warpKey( Object id ) {
        return (ID)id;
    }

    @Override
    public ChannelControlBlock queryChannelById( Object id ) {
        return this.mChannelMapPool.get( this.warpKey( id ) );
    }

    @Override
    public void onlyRemove( Object id ) {
        this.mChannelMapPool.remove( this.warpKey( id ) );
    }

    @Override
    public int size() {
        return this.mChannelMapPool.size();
    }

    @Override
    public void clear() {
        this.mPoolIOLock.writeLock().lock();
        try{
            for( ChannelControlBlock block : this.mChannelMapPool.values() ) {
                block.close();
                block.release();
            }

            this.mChannelMapPool.clear();
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.mChannelMapPool.isEmpty();
    }

    @Override
    public Map getPooledMap() {
        return this.mChannelMapPool;
    }

    @Override
    public void deactivate( ChannelControlBlock ccb ) {
        this.mPoolIOLock.writeLock().lock();
        try {
            ID id = this.warpKey( ccb.getChannel().getChannelID() );
            if( !ccb.getChannel().isShutdown() ) {
                ccb.close();
                ccb.release();
            }
            this.onlyRemove( id );
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
    }

    @Override
    public ChannelControlBlock terminate( Object id ) throws InterruptedException {
        this.mPoolIOLock.writeLock().lock();
        ChannelControlBlock block;
        try {
            block = this.queryChannelById( id );
            if( block != null ) {
                block.close();
                block.release();
                this.onlyRemove( id );
            }
        }
        finally {
            this.mPoolIOLock.writeLock().unlock();
        }
        return block;
    }

    @Override
    public long getMajorWaitTimeout() {
        return this.mnMajorWaitTimeout;
    }

    @Override
    public PassiveRegisterChannelPool setMajorWaitTimeout( long nMillisTimeout ){
        this.mnMajorWaitTimeout = nMillisTimeout;
        return this;
    }

    @Override
    public int getMaximumPoolSize() {
        return this.mnMaximumPoolSize;
    }

    @Override
    public PassiveRegisterChannelPool setIdleChannel( ChannelControlBlock block ) {
        this.addChannel( block ); // TODO
        return this;
    }

    @Override
    public ChannelPool add( ChannelControlBlock block ) {
        this.addChannel( block );
        return null;
    }
}
