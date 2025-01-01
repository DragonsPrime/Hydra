package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.wolfmc.client.MessengerNettyChannelControlBlock;

public abstract class ArchChannelPool implements ChannelPool {
    @Override
    public boolean isAllChannelsTerminated() {
        if( this.isEmpty() ) {
            return true;
        }

        //boolean b = true;
        for ( Object o : this.getPooledChannels() ){
            MessengerNettyChannelControlBlock block = (MessengerNettyChannelControlBlock) o;
            //b = b && block.isShutdown();
            if( !block.isShutdown() ) {
                return false;
            }
        }
        //return b;
        return true;
    }
}
