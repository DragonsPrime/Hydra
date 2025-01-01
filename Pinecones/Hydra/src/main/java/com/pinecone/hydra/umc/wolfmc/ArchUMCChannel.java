package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.MessageNode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;

import java.net.SocketAddress;

public abstract class ArchUMCChannel implements NettyUMCChannel {
    protected ChannelId                  mChannelID          ;
    protected long                       mIdentityID         ;
    protected Thread                     mAffiliateThread    = Thread.currentThread();
    protected MessageNode                mParentMessageNode  ;
    protected ChannelFuture              mLastChannelFuture  ;
    protected Channel                    mChannel            ;

    protected SocketAddress              mAddress            ;
    protected volatile UlfChannelStatus  mChannelStatus      = UlfChannelStatus.IDLE;

    protected ArchUMCChannel( MessageNode node ) {
        this.mParentMessageNode  = node;
    }

    protected ArchUMCChannel( MessageNode node, Channel nativeChannel, SocketAddress address ) {
        this( node );
        this.mChannel            = nativeChannel;
        this.mChannelID          = this.mChannel.id();
        this.mAddress            = address;
    }

    protected ArchUMCChannel( MessageNode node, Channel nativeChannel ) {
        this( node, nativeChannel, null );
    }




    public ArchUMCChannel    setThreadAffinity( Thread affinity ) {
        this.mAffiliateThread = affinity;
        return this;
    }

    public ArchUMCChannel    bindAffiliateThread( Thread affinity ) {
        if( this.mAffiliateThread == null ) {
            return this.setThreadAffinity( affinity );
        }
        return this;
    }

    public synchronized ArchUMCChannel bindThisThread() {
        return this.bindAffiliateThread( Thread.currentThread() );
    }

    public ChannelFuture     getLastChannelFuture() {
        return this.mLastChannelFuture;
    }



    @Override
    public SocketAddress     getAddress(){
        return this.mAddress;
    }

    @Override
    public Thread            getAffiliateThread(){
        return this.mAffiliateThread;
    }

    @Override
    public ChannelId         getChannelID() {
        return this.mChannelID;
    }

    @Override
    public long              getIdentityID() {
        return this.mIdentityID;
    }

    void                     setIdentityID( long identityID ) {
        this.mIdentityID = identityID;
    }

    @Override
    public Channel           getNativeHandle(){
        return this.mChannel;
    }

    @Override
    public UlfChannelStatus  getChannelStatus() {
        return this.mChannelStatus;
    }

    @Override
    public void              setChannelStatus( UlfChannelStatus status ) {
        this.mChannelStatus = status;
    }

    @Override
    public MessageNode       getParentMessageNode() {
        return this.mParentMessageNode;
    }


    @Override
    public void              release() {
        this.mAffiliateThread    = null;
        this.mLastChannelFuture  = null;
//        this.mChannel            = null;
//        this.mChannelStatus      = null;
        this.mParentMessageNode  = null;
    }

    @Override
    public void              close() {
        this.setChannelStatus( UlfChannelStatus.WAITING_FOR_SHUTDOWN );
        this.getNativeHandle().close();
        this.setChannelStatus( UlfChannelStatus.SHUTDOWN );
    }

    @Override
    public boolean           isShutdown() {
        return this.getChannelStatus().isTerminated();
    }
}
