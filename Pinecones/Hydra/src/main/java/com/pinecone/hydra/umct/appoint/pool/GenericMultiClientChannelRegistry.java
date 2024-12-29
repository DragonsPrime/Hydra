package com.pinecone.hydra.umct.appoint.pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.FairChannelPool;
import com.pinecone.hydra.umc.msg.MultiClientChannelRegistry;
import com.pinecone.hydra.umc.wolfmc.UlfIOLoadBalanceStrategy;
import com.pinecone.hydra.umc.wolfmc.UlfIdleFirstBalanceStrategy;
import com.pinecone.hydra.umc.wolfmc.client.ProactiveParallelFairChannelPool;

public class GenericMultiClientChannelRegistry<CID > implements MultiClientChannelRegistry<CID > {
    protected static final UlfIOLoadBalanceStrategy LoadBalanceStrategy = new UlfIdleFirstBalanceStrategy();

    protected Map<CID, FairChannelPool >    mClientChannelRegistry;

    public GenericMultiClientChannelRegistry() {
        this.mClientChannelRegistry = new ConcurrentHashMap<>();
    }

    @Override
    public void register( CID id, ChannelControlBlock controlBlock ) {
        FairChannelPool pool = this.mClientChannelRegistry.get( id );
        if ( pool == null ) {
            pool = new ProactiveParallelFairChannelPool<>( LoadBalanceStrategy );
            this.mClientChannelRegistry.put( id, pool );
        }

        pool.add( controlBlock );
    }

    @Override
    public int size() {
        return this.mClientChannelRegistry.size();
    }

    @Override
    public void clear() {
        for( FairChannelPool pool : this.mClientChannelRegistry.values() ) {
            pool.clear(); // All channels should be closed in this method, in principle.
        }
        this.mClientChannelRegistry.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.mClientChannelRegistry.isEmpty();
    }
}
