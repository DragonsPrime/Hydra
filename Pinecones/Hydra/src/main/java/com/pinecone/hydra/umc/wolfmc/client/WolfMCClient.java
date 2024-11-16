package com.pinecone.hydra.umc.wolfmc.client;

import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.wolfmc.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;


/**
 *  Pinecone Ursus For Java WolfClient [ Wolf, Uniform Message Control Protocol Client ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Bean Nuts Walnut Ulfhedinn Wolves/Ulfar Family.
 *  Uniform Message Control Protocol (UMC)
 *    UMC is a simple TCP/IP based binary transmit protocol, using to directly transfer the key-val liked messages based on JSON.
 *    It refers from the HTTP, supported PUT/POST method, a simple replacement of HTTP for transfer cluster control messages.
 *
 *  Uniform Message Control Protocol for WolfMC Service [Client/Server] (Ulf UMC)
 *  Uniform Message Control Protocol for RabbitMQ Client (Rabbit UMC)
 *  etc.
 *  *****************************************************************************************
 */
public class WolfMCClient extends ArchAsyncMessenger {
    protected EventLoopGroup                       mExecutorGroup;
    protected Bootstrap                            mBootstrap;

    protected ClientConnectArguments               mConnectionArguments;
    protected MCSecurityAuthentication             mSecurityAuthentication; //TODO

    protected UlfAsyncMsgHandleAdapter             mPrimeAsyncMessageHandler = new UnsetUlfAsyncMsgHandleAdapter( this ); // For all channels.

    public WolfMCClient( String szName, Hydrarum system, UlfMessageNode parent, JSONObject joConf, ExtraHeadCoder extraHeadCoder ){
        super( szName, system, parent, joConf, extraHeadCoder );

        this.apply( joConf );
    }

    public WolfMCClient( String szName, Hydrarum system, JSONObject joConf, ExtraHeadCoder extraHeadCoder ){
        this( szName, system, null, joConf, extraHeadCoder );
    }

    public WolfMCClient( String szName, Hydrarum system, JSONObject joConf ){
        this( szName, system, joConf, null );
    }

    public WolfMCClient( String szName, UlfMessageNode parent, Hydrarum system, JSONObject joConf ){
        this( szName, system, parent, joConf, null );
    }


    @Override
    public WolfMCClient                   apply( JSONObject joConf ) {
        super.apply( joConf );
        this.mConnectionArguments = new ClientConnectionArguments( joConf );

        return this;
    }

    @Override
    public WolfMCClient                   apply( UlfAsyncMsgHandleAdapter fnAsyncMessageAdapter ) {
        this.mPrimeAsyncMessageHandler = fnAsyncMessageAdapter;

        return this;
    }

    public ClientConnectArguments         getConnectionArguments() {
        return this.mConnectionArguments;
    }

    public EventLoopGroup                 getEventLoopGroup() {
        return this.mExecutorGroup;
    }

    public Bootstrap                      getBootstrap() {
        return this.mBootstrap;
    }

    public int                            getParallelChannels() {
        return this.getConnectionArguments().getParallelChannels();
    }

    protected void                        clear(){
        this.mChannelPool.clear();
    }

    public void                           close() throws ProvokeHandleException{
        this.mStateMutex.lock();
        try{
            if( this.mExecutorGroup != null ) {
                this.mExecutorGroup.shutdownGracefully();
                this.clear();
                this.mShutdown = true;
            }
        }
        finally {
            this.mStateMutex.unlock();
        }

        try{
            synchronized ( this.mPrimaryThreadJoinMutex ) {
                WolfMCClient.this.mPrimaryThreadJoinMutex.notify();
            }
        }
        catch ( IllegalMonitorStateException e ) {
            throw new ProvokeHandleException( "IllegalMonitorStateException [WolfMCClient::close], this exception has been redirected to parent thread.", e );
        }
    }

    @Override
    public void                           kill() {
        try{
            this.close();
        }
        catch ( ProvokeHandleException e ) {
            super.kill(); // Kill master thread forcefully.
            this.clear();
        }
    }

    protected MessengerNettyChannelControlBlock syncSpawnSoloChannel() throws IOException {
        MessengerNettyChannelControlBlock ccb = null;
        ccb                                   = new MessengerNettyChannelControlBlock( this );
        ChannelFuture future                  = ccb.getChannel().toConnect(
                new InetSocketAddress( this.getConnectionArguments().getHost(), this.getConnectionArguments().getPort() )
        ).getLastChannelFuture();
        ccb.getChannel().getNativeHandle().attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY ) ).set( ccb );
        this.getTaskManager().add( ccb );

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete( ChannelFuture channelFuture ) throws Exception {
                synchronized ( WolfMCClient.this.mPrimaryThreadJoinMutex ) {
                    WolfMCClient.this.mShutdown = !channelFuture.isSuccess();
                    WolfMCClient.this.mPrimaryThreadJoinMutex.notify();
                }
            }
        });
        //channel.closeFuture().sync();
        this.getChannelPool().pushBack( ccb );

        synchronized ( this.mPrimaryThreadJoinMutex ) {
            try {
                this.mPrimaryThreadJoinMutex.wait( this.getConnectionArguments().getSocketTimeout() );
                if( this.mShutdown ) {
                    throw new UnknownHostException( "Connect failed with '" + this.getConnectionArguments().getHost() + ":" + this.getConnectionArguments().getPort() + "'" );
                }
            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }

        return ccb;
    }

    protected void                        syncSpawnChannels() throws IOException {
        int n = this.getConnectionArguments().getParallelChannels();

        for ( int i = 0; i < n; i++ ) {
            MessengerNettyChannelControlBlock block = this.syncSpawnSoloChannel();
            this.infoLifecycle( String.format( "Channel%d(%s)", i, block.getChannel().getChannelID() ), "Spawned" );
        }
    }

    protected void                        invokeChannelOwnedOnError( ChannelHandlerContext ctx, Throwable cause ) {
        UlfAsyncMsgHandleAdapter handle = (UlfAsyncMsgHandleAdapter)ctx.channel().attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY ) ).get();
        if( handle == null ) {
            handle = WolfMCClient.this.mPrimeAsyncMessageHandler;
        }
        handle.onError( ctx, cause );
    }

    protected void                        handleArrivedMessage( UlfAsyncMsgHandleAdapter handle, Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
        if( this.getErrorMessageAudit().isErrorMessage( msg ) ) {
            handle.onErrorMsgReceived( medium, block, msg, ctx, msg );
        }
        else {
            handle.onSuccessfulMsgReceived( medium, block, msg, ctx, msg );
        }
    }

    protected void                        initNettySubsystem() throws IOException {
        this.mExecutorGroup = new NioEventLoopGroup();
        this.mBootstrap     = new Bootstrap();
        Bootstrap bootstrap = this.mBootstrap;
        bootstrap.group  ( this.mExecutorGroup    );
        bootstrap.channel( NioSocketChannel.class );
        bootstrap.option ( ChannelOption.CONNECT_TIMEOUT_MILLIS, this.getConnectionArguments().getSocketTimeout() );
        bootstrap.handler( new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel( SocketChannel sc ) throws Exception {
                sc.pipeline().addLast( new ReadTimeoutHandler( WolfMCClient.this.getConnectionArguments().getKeepAliveTimeout(), TimeUnit.SECONDS ) );
                sc.pipeline().addLast( new GenericUMCByteMessageDecoder( WolfMCClient.this.getExtraHeadCoder() ) );
                sc.pipeline().addLast( new ChannelInboundHandlerAdapter (){
                    @Override
                    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
                        super.channelActive(ctx);

                        //UlfChannelControlBlock channel = WolfMCClient.this.getChannelPool().queryChannelById( ctx.channel().id() );
                        MessengerNettyChannelControlBlock channel = (MessengerNettyChannelControlBlock)ctx.channel().attr(
                                AttributeKey.valueOf(WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY)
                        ).get();

                        channel.afterConnectionArrive(
                                new AsyncUlfMedium( ctx, null, WolfMCClient.this ),  false
                        );
                        channel.setThreadAffinity( Thread.currentThread() );
                        synchronized ( WolfMCClient.this.mPrimaryThreadJoinMutex ) {
                            WolfMCClient.this.mPrimaryThreadJoinMutex.notify();
                        }
                    }

                    @Override
                    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
                        Medium medium          = new AsyncUlfMedium( ctx, (ByteBuf) msg, WolfMCClient.this );
                        UlfMCReceiver receiver = new UlfMCReceiver( medium );
                        UMCMessage message     = receiver.readMsg();

                        MessengerNettyChannelControlBlock channelControlBlock = (MessengerNettyChannelControlBlock)ctx.channel().attr(
                                AttributeKey.valueOf( WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY )
                        ).get();

                        //Debug.trace( channelControlBlock.getChannel().getChannelID() );
                        if( channelControlBlock.getChannelStatus() == UlfChannelStatus.FORCE_SYNCHRONIZED ){
                            channelControlBlock.getSyncRetMsgQueue().add( message );
                            //WolfMCClient.this.mSyncRetMsgQueue.add( message );
                        }
                        else {
                            UlfAsyncMsgHandleAdapter handle = (UlfAsyncMsgHandleAdapter)ctx.channel().attr(
                                    AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY )
                            ).get();
                            if( handle != null ) {
                                WolfMCClient.this.handleArrivedMessage( handle, medium, channelControlBlock, message, ctx, msg );
                                ctx.channel().attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY ) ).set( null ); // For another channel to reset, likes ajax.
                            }
                            else {
                                WolfMCClient.this.handleArrivedMessage( WolfMCClient.this.mPrimeAsyncMessageHandler, medium, channelControlBlock, message, ctx, msg );
                            }

                            WolfMCClient.this.getChannelPool().setIdleChannel( channelControlBlock );
                        }

                        medium.release();
                        medium = new AsyncUlfMedium( ctx, null, WolfMCClient.this );
                        channelControlBlock.afterConnectionArrive( medium,  true );
                    }

                    @Override
                    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
                        if( WolfMCClient.this.getChannelPool().isAllChannelsTerminated() ) {
                            try{
                                Debug.warn( "All channels has been terminated, client terminating." );
                                WolfMCClient.this.close();
                            }
                            catch ( ProvokeHandleException e ) {
                                WolfMCClient.this.kill(); // Those should never happened, just unconditional shutdown.
                            }

                            return;
                        }

                        MessengerNettyChannelControlBlock ccb = (MessengerNettyChannelControlBlock)ctx.channel().attr(
                                AttributeKey.valueOf( WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY )
                        ).get();
                        WolfMCClient.this.getChannelPool().deactivate( ccb );
                        WolfMCClient.this.getMajorIOLock().lock();
                        try{
                            WolfMCClient.this.getTaskManager().erase( ccb );
                        }
                        finally {
                            WolfMCClient.this.getMajorIOLock().unlock();
                        }
                    }

                    @Override
                    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
                        WolfMCClient.this.invokeChannelOwnedOnError( ctx, cause );
                    }
                } );
            }

            @Override
            public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
                WolfMCClient.this.invokeChannelOwnedOnError( ctx, cause );
            }
        });

        this.syncSpawnChannels();
        this.infoLifecycle( "Wolf<\uD83D\uDC3A>::initNettySubsystem", "Successfully" );
    }

    public void                           connect() throws IOException {
        this.mStateMutex.lock();

        try{
            if( this.isShutdown() ) {
                this.initNettySubsystem(); // Exception thrown and truncating next detach-mutex-release, redirecting to primary thread.
            }
        }
        finally {
            this.mStateMutex.unlock();
            WolfMCClient.this.unlockOuterThreadDetachMutex();  // This lock shouldn`t be released in `finally`, waiting for primary thread to process.
        }

        synchronized ( this.mPrimaryThreadJoinMutex ) {
            try {
                this.mPrimaryThreadJoinMutex.wait( ); // Join the primary thread.
            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void                           execute() throws IOException {
        Exception[] lastException = new Exception[] { null };
        Thread primaryThread      = new Thread( new Runnable() {
            @Override
            public void run() {
                WolfMCClient.this.getTaskManager().notifyExecuting( WolfMCClient.this );
                try{
                    WolfMCClient.this.connect();
                }
                catch ( Exception e ) {
                    lastException[0] = e;
                    WolfMCClient.this.kill();
                }
                finally {
                    WolfMCClient.this.getTaskManager().notifyFinished( WolfMCClient.this );
                    WolfMCClient.this.unlockOuterThreadDetachMutex();
                }
            }
        });

        this.preparePrimaryThread( primaryThread );
        primaryThread.start();

        this.joinOuterThread();
        this.redirectIOException2ParentThread( lastException[0] );
    }


    public UMCMessage                     sendSyncMsg( UMCMessage request ) throws IOException {
        return this.sendSyncMsg( request, false );
    }

    public UMCMessage                     sendSyncMsg( UMCMessage request, boolean bNoneBuffered ) throws IOException {
        return this.sendSyncMsg( request, bNoneBuffered, this.getConnectionArguments().getKeepAliveTimeout() * 1000 );
    }

    public void                           sendAsynMsg( UMCMessage request ) throws IOException {
        this.sendAsynMsg( request, false );
    }

    @Override
    public void                           sendAsynMsg( UMCMessage request, UlfAsyncMsgHandleAdapter handler ) throws IOException {
        this.sendAsynMsg( request, false, handler );
    }

}
