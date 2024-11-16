package com.pinecone.hydra.umc.wolfmc.client;

import com.pinecone.hydra.umc.msg.*;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.wolfmc.*;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;
import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ArchAsyncMessenger extends WolfMCNode implements AsyncMessenger, UlfMessageNode {
    protected final ReentrantLock                              mSynRequestLock  = new ReentrantLock();
    protected ProactiveParallelFairChannelPool<ChannelId >     mChannelPool     ;
    //protected BlockingDeque<UMCMessage>                        mSyncRetMsgQueue = new LinkedBlockingDeque<>();

    public ArchAsyncMessenger( String szName, Hydrarum system, UlfMessageNode parent, JSONObject joConf, ExtraHeadCoder extraHeadCoder ) {
        super( szName, system, parent, joConf, extraHeadCoder );

        this.mChannelPool   = new ProactiveParallelFairChannelPool<>( this, new UlfIdleFirstBalanceStrategy() ); //TODO
        //this.makeNameAndId();
    }

    public ArchAsyncMessenger( String szName, Hydrarum system, JSONObject joConf, ExtraHeadCoder extraHeadCoder ) {
        this( szName, system, null, joConf, extraHeadCoder );
    }


    @Override
    public ProactiveParallelFairChannelPool   getChannelPool() {
        return this.mChannelPool;
    }

    Lock                                      getSynRequestLock() {
        return this.mSynRequestLock;
    }

    UlfAsyncMessengerChannelControlBlock      nextSynChannelCB() {
        UlfAsyncMessengerChannelControlBlock block = (UlfAsyncMessengerChannelControlBlock) this.getChannelPool().nextSyncChannel( this.getChannelPool().getMajorWaitTimeout() * 2 );
        if( block == null ) {
            throw new ChannelAllocateException( "Channel allocate failed." );
        }
        ArchAsyncMessenger.reconnect( block );
        return block;
    }

    UlfAsyncMessengerChannelControlBlock      nextAsyChannelCB() {
        UlfAsyncMessengerChannelControlBlock block = (UlfAsyncMessengerChannelControlBlock) this.getChannelPool().nextAsynChannel( this.getChannelPool().getMajorWaitTimeout() * 2 );
        if( block == null ) {
            throw new ChannelAllocateException( "Channel allocate failed." );
        }
        ArchAsyncMessenger.reconnect( block );
        return block;
    }

//    BlockingDeque<UMCMessage >                getSyncRetMsgQueue() {
//        return this.mSyncRetMsgQueue;
//    }


    @Override
    public UMCMessage sendSyncMsg( UMCMessage request, boolean bNoneBuffered, long nWaitTime ) throws IOException {
        return this.nextSynChannelCB().sendSyncMsg( request, bNoneBuffered, nWaitTime );
    }

    @Override
    public void sendAsynMsg( UMCMessage request, boolean bNoneBuffered ) throws IOException {
        this.nextAsyChannelCB().sendAsynMsg( request, bNoneBuffered );
    }

    @Override
    public void sendAsynMsg( UMCMessage request, boolean bNoneBuffered, UlfAsyncMsgHandleAdapter handler ) throws IOException {
        UlfAsyncMessengerChannelControlBlock cb = this.nextAsyChannelCB();
        cb.getChannel().getNativeHandle().attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY ) ).set( handler );
        cb.sendAsynMsg( request, bNoneBuffered );
    }

    static void reconnect( ChannelControlBlock block ) throws ProvokeHandleException {
        if( block.isShutdown() ) {
            try{
                block.getChannel().reconnect();
                ( (UlfMessageNode)block.getParentMessageNode() ).getChannelPool().setIdleChannel( block );
            }
            catch ( IOException e ) {
                throw new ProvokeHandleException( e );
            }
        }
    }

}
