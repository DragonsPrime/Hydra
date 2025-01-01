package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

import org.slf4j.Logger;

import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.system.component.Slf4jTraceable;
import com.pinecone.hydra.umc.msg.ChannelAllocateException;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.msg.FairChannelPool;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.MultiClientChannelRegistry;
import com.pinecone.hydra.umc.msg.RecipientChannelControlBlock;
import com.pinecone.hydra.umc.msg.UMCChannel;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfChannelStatus;
import com.pinecone.hydra.umc.wolfmc.UlfMessageNode;
import com.pinecone.hydra.umc.wolfmc.WolfMCStandardConstants;
import com.pinecone.hydra.umc.wolfmc.server.RecipientNettyChannelControlBlock;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.ServiceInternalException;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.UlfConnection;
import com.pinecone.hydra.umct.appoint.pool.GenericMultiClientChannelRegistry;
import com.pinecone.hydra.umct.husky.HuskyCTPConstants;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public abstract class ArchDuplexExpress implements DuplexExpress, MessageExpress, Slf4jTraceable {
    protected Logger                            mLogger      ;
    protected MultiClientChannelRegistry<Long > mMultiClientChannelRegistry;

    protected ArchDuplexExpress() {
        this.mMultiClientChannelRegistry = new GenericMultiClientChannelRegistry<>();
    }

    public ArchDuplexExpress( Logger logger ) {
        this();
        this.mLogger = logger;
    }

    @Override
    public Logger getLogger() {
        return this.mLogger;
    }

    protected UMCConnection wrap( Package that ) {
        return (UMCConnection) that;
    }

    @Override
    public UMCMessage processResponse( UMCMessage request, UMCMessage response ) {
        if ( request.getHead().getControlBits() == HuskyCTPConstants.HCTP_DUP_CONTROL_PASSIVE_REQUEST ) {
            response.getHead().setControlBits( HuskyCTPConstants.HCTP_DUP_CONTROL_PASSIVE_RESPONSE );
        }

        return response;
    }

    protected abstract void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception ;

    protected boolean handleDuplexControlMessage( UMCConnection connection, Object[] args ) throws Exception {
        if ( this.interceptPassiveChannel( connection, args ) ) {
            return true;
        }
        if ( this.interceptHandlePassiveResponse( connection, args ) ) {
            return true;
        }

        return false;
    }

    @Override
    public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
        UlfConnection connection = new UlfConnection(  medium, msg, transmit, receiver, args );
        if ( this.handleDuplexControlMessage( connection, args ) ) {
            return;
        }

        this.onSuccessfulMsgReceived( connection, args );
    }

    protected boolean interceptHandlePassiveResponse( UMCConnection connection, Object[] args ) throws ServiceInternalException {
        UMCConnection uc          = this.wrap( connection );
        UMCMessage msg            = uc.getMessage();
        long controlBits          = msg.getHead().getControlBits();

        // Notice:
        // For duplex passive channel, it is necessary to use control-bits markers and explicitly call the `handler`, otherwise it will be intercepted by the `express`.
        // 对于双工被动链路，必须走VIP通道使用控制位标记，并显式调用绑定的回调函数，不然会被总线错误拦截。

        if ( controlBits == HuskyCTPConstants.HCTP_DUP_CONTROL_PASSIVE_RESPONSE ) {
            RecipientChannelControlBlock cb = (RecipientChannelControlBlock)args[ 0 ];
            Channel channel = (Channel)cb.getChannel().getNativeHandle();

            UlfAsyncMsgHandleAdapter handle = (UlfAsyncMsgHandleAdapter) channel.attr(
                    AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY )
            ).get();

            try{
                handle.onSuccessfulMsgReceived( connection.getMessageSource(), connection.getTransmit(), connection.getReceiver(), msg, args );
            }
            catch ( Exception e ) {
                throw new ServiceInternalException( e );
            }

            return true;
        }
        return false;
    }


    protected boolean interceptPassiveChannel( UMCConnection connection, Object[] args ) {
        UMCConnection uc          = this.wrap( connection );
        UMCMessage msg            = uc.getMessage();
        long controlBits          = msg.getHead().getControlBits();
        if ( controlBits == HuskyCTPConstants.HCTP_DUP_CONTROL_REGISTER ) {
            this.registerPassiveChannel( uc, connection, args );
            return true;
        }

        return false;
    }

    protected void registerPassiveChannel( UMCConnection uc, UMCConnection connection, Object[] args ) {
        ChannelControlBlock ccb = (ChannelControlBlock) args[ 0 ];
        UMCChannel channel = ccb.getChannel();
        long                cid = channel.getIdentityID();

        this.mMultiClientChannelRegistry.register( cid, ccb );
        this.getLogger().info( "[PassiveChannel] [ClientId: {}, ChannelId: {}] <{}>", cid, ccb.getChannel().getChannelID(), "Registered" );
    }


    static void reconnect( ChannelControlBlock block ) throws IOException {
        if( block.isShutdown() ) {
            block.getChannel().reconnect();
            ( (UlfMessageNode)block.getParentMessageNode() ).getChannelPool().setIdleChannel( block );
        }
    }

    RecipientNettyChannelControlBlock nextAsyChannelCB( FairChannelPool pool ) throws IOException {
        RecipientNettyChannelControlBlock block = (RecipientNettyChannelControlBlock) pool.nextAsynChannel( pool.getMajorWaitTimeout() * 2 );
        if( block == null ) {
            throw new ChannelAllocateException( "Channel allocate failed." );
        }
        HuskyDuplexExpress.reconnect( block );
        return block;
    }

    @Override
    public ChannelPool getPoolByClientId( long clientId ) {
        return this.mMultiClientChannelRegistry.getPool( clientId );
    }

    @Override
    public void sendAsynMsg( long clientId, UMCMessage request, boolean bNoneBuffered, UlfAsyncMsgHandleAdapter handler ) throws IOException, IllegalArgumentException {
        try{
            ChannelPool pool = this.mMultiClientChannelRegistry.getPool( clientId );
            if ( pool == null ) {
                throw new IllegalArgumentException( "No such client " + clientId );
            }

            FairChannelPool fp = (FairChannelPool) pool;
            RecipientNettyChannelControlBlock cb = this.nextAsyChannelCB( fp );
            cb.getChannel().getNativeHandle().attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY ) ).set( handler );
            cb.getChannel().setChannelStatus( UlfChannelStatus.WAITING_PASSIVE_SEND );
            cb.getTransmit().sendMsg( request, bNoneBuffered );
            cb.getChannel().setChannelStatus( UlfChannelStatus.WAITING_PASSIVE_RECEIVE );
        }
        catch ( ChannelAllocateException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public void sendAsynMsg( long clientId, UMCMessage request, boolean bNoneBuffered, AsynMsgHandler handler ) throws IOException {
        this.sendAsynMsg( clientId, request, bNoneBuffered, AsynMsgHandler.wrap( handler ) );
    }
}