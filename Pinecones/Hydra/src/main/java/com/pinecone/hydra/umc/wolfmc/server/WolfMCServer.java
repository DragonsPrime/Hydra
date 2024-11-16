package com.pinecone.hydra.umc.wolfmc.server;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.hydra.system.component.LogStatuses;
import com.pinecone.hydra.umc.msg.*;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.wolfmc.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  Pinecone Ursus For Java WolfServer [ Wolf, Uniform Message Control Protocol Server ]
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
public class WolfMCServer extends WolfMCNode implements Recipient {
    protected ServerConnectArguments                          mConnectionArguments ;

    protected EventLoopGroup                                  mMasterEventGroup    ;
    protected EventLoopGroup                                  mWorkersEventGroup   ;
    protected ServerBootstrap                                 mBootstrap           ;
    protected ChannelFuture                                   mPrimaryBindFuture   ;
    protected SocketAddress                                   mPrimaryBindAddress  ;
    protected PassiveRegisterChannelPool<ChannelId >          mChannelPool         ;

    protected UlfAsyncMsgHandleAdapter                        mRecipientMsgHandler ;

    private final ReentrantLock                               mSynRequestLock      = new ReentrantLock(); // For inner purposes.

    public WolfMCServer( String szName, Hydrarum system, UlfMessageNode parent, JSONObject joConf, ExtraHeadCoder extraHeadCoder ) {
        super( szName, system, parent, joConf, extraHeadCoder );
        this.apply( joConf );
    }

    public WolfMCServer( String szName, Hydrarum system, JSONObject joConf, ExtraHeadCoder extraHeadCoder ) {
        this( szName, system, null, joConf, extraHeadCoder );
    }

    public WolfMCServer( String szName, Hydrarum system, UlfMessageNode parent, JSONObject joConf ) {
        this( szName, system, parent, joConf, null );
    }

    public WolfMCServer( String szName, Hydrarum system, JSONObject joConf ) {
        this( szName, system, null, joConf );
    }


    @Override
    public WolfMCServer apply( JSONObject joConf ) {
        super.apply( joConf );
        this.mConnectionArguments = new ServerConnectionArguments( joConf );
        this.mChannelPool         = new PassiveRegisterChannelPool<>(
                this, new UlfIdleFirstBalanceStrategy(), joConf.optInt( "MaximumConnections", (int)1e7 )
        );

        try{
            String szRecipientMsgHandler   = joConf.optString( "RecipientMsgHandler" );
            if( StringUtils.isEmpty( szRecipientMsgHandler ) ) {
                this.mRecipientMsgHandler  = new UnsetUlfAsyncMsgHandleAdapter( this ) ;
            }
            else {
                this.mRecipientMsgHandler  = (UlfAsyncMsgHandleAdapter) DynamicFactory.DefaultFactory.loadInstance(
                        szRecipientMsgHandler, null, null
                );
            }
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            throw new ProxyProvokeHandleException( e );
        }

        return this;
    }

    @Override
    public WolfMCServer apply( UlfAsyncMsgHandleAdapter fnRecipientMsgHandler ) {
        this.mRecipientMsgHandler = fnRecipientMsgHandler;

        return this;
    }

    @Override
    public int getMaximumConnections() {
        return this.mChannelPool.getMaximumPoolSize();
    }

    public void close() throws ProvokeHandleException {
        this.mStateMutex.lock();
        try{
            if( this.mMasterEventGroup != null ) {
                this.mMasterEventGroup.shutdownGracefully();
                //this.clear();
                this.mShutdown = true;
            }

            if( this.mWorkersEventGroup != null ) {
                this.mWorkersEventGroup.shutdownGracefully();
            }
        }
        finally {
            this.mStateMutex.unlock();
        }

        try{
            synchronized ( this.mPrimaryThreadJoinMutex ) {
                WolfMCServer.this.mPrimaryThreadJoinMutex.notify();
            }
        }
        catch ( IllegalMonitorStateException e ) {
            throw new ProvokeHandleException( "IllegalMonitorStateException [WolfMCClient::close], this exception has been redirected to parent thread.", e );
        }
    }

    @Override
    public void  kill() {
        try{
            this.close();
        }
        catch ( ProvokeHandleException e ) {
            super.kill(); // Kill master thread forcefully.
            //this.clear();
        }
    }

    protected void handleArrivedMessage( UlfAsyncMsgHandleAdapter handle, Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
        if( this.getErrorMessageAudit().isErrorMessage( msg ) ) {
            handle.onErrorMsgReceived( medium, block, msg, ctx, msg );
        }
        else {
            handle.onSuccessfulMsgReceived( medium, block, msg, ctx, msg );
        }
    }


    protected void initNettySubsystem() throws IOException {
        this.mMasterEventGroup    = new NioEventLoopGroup();
        this.mWorkersEventGroup   = new NioEventLoopGroup();
        this.mBootstrap           = new ServerBootstrap();

        this.mBootstrap.group   ( this.mMasterEventGroup , this.mWorkersEventGroup );
        this.mBootstrap.channel ( NioServerSocketChannel.class );
        this.mBootstrap.option  ( ChannelOption.SO_BACKLOG, 1024 );
        this.mBootstrap.childHandler( new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel( SocketChannel sc ) throws Exception {
                sc.pipeline().addLast( new ReadTimeoutHandler( 1000, TimeUnit.SECONDS ) );
                sc.pipeline().addLast( new GenericUMCByteMessageDecoder( WolfMCServer.this.getExtraHeadCoder() ) );

                sc.pipeline().addLast( new ChannelInboundHandlerAdapter (){
                    @Override
                    public void channelActive( ChannelHandlerContext ctx ) throws Exception {
                        super.channelActive(ctx);

                        RecipientNettyChannelControlBlock ccb = new RecipientNettyChannelControlBlock(
                                WolfMCServer.this, ctx.channel(), false
                        );

                        ccb.getChannel().getNativeHandle().attr(
                                AttributeKey.valueOf( WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY )
                        ).set( ccb );

                        WolfMCServer.this.getLogger().info( "[MessengerConnected] <id:`{}`>", ctx.channel().id() );

                        ccb.afterConnectionArrive(
                                new AsyncUlfMedium( ctx, null, WolfMCServer.this ),  false
                        );
                        ccb.setThreadAffinity( Thread.currentThread() );
                        WolfMCServer.this.getTaskManager().add( ccb );
                        WolfMCServer.this.mChannelPool.setIdleChannel( ccb );
                    }

                    @Override
                    public void channelRead( ChannelHandlerContext ctx, Object msg ) throws Exception {
                        Medium medium = new AsyncUlfMedium( ctx, (ByteBuf) msg, WolfMCServer.this );
                        UlfMCReceiver receiver = new UlfMCReceiver( medium );
                        UMCMessage message = receiver.readMsg();

                        RecipientNettyChannelControlBlock channelControlBlock = (RecipientNettyChannelControlBlock)ctx.channel().attr(
                                AttributeKey.valueOf( WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY )
                        ).get();

                        WolfMCServer.this.handleArrivedMessage(
                                WolfMCServer.this.mRecipientMsgHandler, medium, channelControlBlock, message, ctx, msg
                        );

                        medium.release();
                        medium = new AsyncUlfMedium( ctx, null, WolfMCServer.this );
                        channelControlBlock.afterConnectionArrive( medium,  true );
                    }

                    @Override
                    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
                        RecipientNettyChannelControlBlock ccb = (RecipientNettyChannelControlBlock)ctx.channel().attr(
                                AttributeKey.valueOf( WolfMCStandardConstants.CB_CONTROL_BLOCK_KEY )
                        ).get();

                        WolfMCServer.this.mChannelPool.deactivate( ccb );
                        WolfMCServer.this.getMajorIOLock().lock();
                        try{
                            WolfMCServer.this.getTaskManager().erase( ccb );
                        }
                        finally {
                            WolfMCServer.this.getMajorIOLock().unlock();
                        }
                        WolfMCServer.this.getLogger().info( "[MessengerDetached] <id:`{}`>", ctx.channel().id() );
                    }

                    @Override
                    public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
                        WolfMCServer.this.mRecipientMsgHandler.onError( ctx, cause );
                    }
                } );
            }

            @Override
            public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause ) throws Exception {
                WolfMCServer.this.mRecipientMsgHandler.onError( ctx, cause );
            }
        });

        String szHost           = this.getConnectionArguments().getHost();
        short  nPort            = this.getConnectionArguments().getPort();
        if( StringUtils.isEmpty( szHost ) ) {
            this.mPrimaryBindAddress = new InetSocketAddress( nPort );
        }
        else {
            this.mPrimaryBindAddress = new InetSocketAddress( szHost, nPort );
        }
        this.mPrimaryBindFuture = this.mBootstrap.bind( this.mPrimaryBindAddress );

        this.mPrimaryBindFuture.addListener( new ChannelFutureListener() {
            @Override
            public void operationComplete( ChannelFuture channelFuture ) throws Exception {
                synchronized ( WolfMCServer.this.mPrimaryThreadJoinMutex ) {
                    WolfMCServer.this.mShutdown = !channelFuture.isSuccess();
                    WolfMCServer.this.mPrimaryThreadJoinMutex.notify();
                }
            }
        } );

        synchronized ( this.mPrimaryThreadJoinMutex ) {
            try {
                this.mPrimaryThreadJoinMutex.wait( this.getConnectionArguments().getSocketTimeout() );
                if( this.mShutdown ) {
                    throw new BindException( String.format( "%s [Serve], binding `%s` compromised.", this.className(), this.mPrimaryBindAddress.toString() ) );
                }
            }
            catch ( InterruptedException e ) {
                this.getSystem().handleLiveException( e );
            }
        }

        /*try {
            this.mPrimaryBindFuture.channel().closeFuture().sync();
        }
        catch ( InterruptedException e ) {
            throw new RuntimeException(e);
        }*/
    }

    public void serve() throws IOException {
        this.mStateMutex.lock();

        try{
            if( this.isShutdown() ) {
                this.initNettySubsystem(); // Exception thrown and truncating next detach-mutex-release, redirecting to primary thread.
            }
        }
        finally {
            this.mStateMutex.unlock();
            WolfMCServer.this.unlockOuterThreadDetachMutex();  // This lock shouldn`t be released in `finally`, waiting for primary thread to process.
        }

        synchronized ( this.mPrimaryThreadJoinMutex ) {
            try {
                this.mPrimaryThreadJoinMutex.wait(); // Join the primary thread.
            }
            catch ( InterruptedException e ) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void execute() throws IOException {
        Exception[] lastException = new Exception[] { null };
        Thread primaryThread      = new Thread( new Runnable() {
            @Override
            public void run() {
                WolfMCServer.this.getTaskManager().notifyExecuting( WolfMCServer.this );
                try{
                    WolfMCServer.this.serve();
                }
                catch ( Exception e ) {
                    lastException[0] = e;
                    WolfMCServer.this.kill();
                }
                finally {
                    WolfMCServer.this.getTaskManager().notifyFinished( WolfMCServer.this );
                    WolfMCServer.this.unlockOuterThreadDetachMutex();
                }
            }
        });

        this.preparePrimaryThread( primaryThread );
        primaryThread.start();

        this.joinOuterThread();
        if( !this.isShutdown() ) {
            this.infoLifecycle( String.format( "Wolf<\uD83D\uDC3A>::BindServer(%s)", this.mPrimaryBindAddress.toString() ), "Successfully" );
        }
        this.redirectIOException2ParentThread( lastException[0] );
    }

    protected Lock getSynRequestLock() {
        return this.mSynRequestLock;
    }

    public ServerConnectArguments getConnectionArguments() {
        return this.mConnectionArguments;
    }

    @Override
    public ChannelPool getChannelPool() {
        return null;
    }
}
