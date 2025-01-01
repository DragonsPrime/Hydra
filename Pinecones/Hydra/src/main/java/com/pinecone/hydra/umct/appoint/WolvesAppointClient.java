package com.pinecone.hydra.umct.appoint;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.slf4j.Logger;

import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfInstructMessage;
import com.pinecone.hydra.umc.wolfmc.WolfMCStandardConstants;
import com.pinecone.hydra.umc.wolfmc.client.UlfAsyncMessengerChannelControlBlock;
import com.pinecone.hydra.umc.wolfmc.client.UlfClient;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.UMCTExpressHandler;
import com.pinecone.hydra.umct.husky.HuskyCTPConstants;
import com.pinecone.hydra.umct.husky.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.machinery.HuskyContextMachinery;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcher;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcherFabricator;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;
import com.pinecone.hydra.umct.mapping.BytecodeControllerInspector;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;
import javassist.ClassPool;

public class WolvesAppointClient extends WolfAppointClient implements DuplexAppointClient {
    protected static Class<?> checkExpressType( Class<?> expressType ) {
        if ( !DuplexExpress.class.isAssignableFrom( expressType ) ) {
            throw new IllegalArgumentException( "`" + expressType.getSimpleName() + "` is not DuplexExpress calibre qualified." );
        }
        return expressType;
    }

    protected Map<ChannelId, ChannelControlBlock > mInstructedChannels;  // Standby controlled channels, waiting for server to instruct.
    protected RouteDispatcher                      mRouteDispatcher;


    protected WolvesAppointClient( UlfClient messenger, RouteDispatcher dispatcher ){
        super( messenger, dispatcher.getInterfacialCompiler(), dispatcher.getContextMachinery().getControllerInspector() );
        this.mRouteDispatcher = dispatcher;
        this.mInstructedChannels = new LinkedTreeMap<>();
    }

    public WolvesAppointClient( UlfClient messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( compiler, controllerInspector, express ) );
        this.apply( express );
    }

    public WolvesAppointClient( UlfClient messenger, CompilerEncoder encoder, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( encoder, express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolvesAppointClient( UlfClient messenger, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolvesAppointClient( UlfClient messenger, Class<?> expressType ){
        super( messenger, true );

        try{
            Constructor<?> constructor = WolvesAppointClient.checkExpressType( expressType ).getConstructor( String.class, MessageJunction.class, Logger.class );
            UMCTExpress express = (UMCTExpress) constructor.newInstance( AppointServer.DefaultEntityName, this, this.getLogger() );

            this.mRouteDispatcher = new HuskyRouteDispatcher( express, messenger.getTaskManager().getClassLoader() );
            HuskyRouteDispatcherFabricator.afterConstructed( (HuskyRouteDispatcher)this.mRouteDispatcher, express );
            this.mPMCTContextMachinery = new HuskyContextMachinery( new BytecodeIfacCompiler(
                    ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
            ), new BytecodeControllerInspector(
                    ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
            ), new GenericFieldProtobufDecoder() );
            this.apply( express );
            this.mInstructedChannels = new LinkedTreeMap<>();
        }
        catch ( NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            throw new IllegalArgumentException( "`" + expressType.getSimpleName() + "` is not UMCTExpress calibre qualified." );
        }
    }

    public WolvesAppointClient( UlfClient messenger ){
        this( messenger, HuskyDuplexExpress.class );
    }



    public void apply( UMCTExpress handler ) {
        this.mRouteDispatcher.setUMCTExpress( handler );
    }

    @Override
    public RouteDispatcher getRouteDispatcher() {
        return this.mRouteDispatcher;
    }

    @Override
    public boolean supportDuplex() {
        return true;
    }

    @Override
    public void embraces( int nLine, UlfAsyncMsgHandleAdapter handler ) throws IOException {
        // Join us, embracing uniformity.

        this.createPassiveChannel( nLine );
        for ( Map.Entry<ChannelId, ChannelControlBlock > kv : this.mInstructedChannels.entrySet() ) {
            UlfInstructMessage instructMessage = new UlfInstructMessage( HuskyCTPConstants.HCTP_DUP_CONTROL_REGISTER );
            instructMessage.getHead().setIdentityId( this.mMessenger.getMessageNodeId() );

            ChannelControlBlock ccb = kv.getValue();
            UlfAsyncMessengerChannelControlBlock cb = (UlfAsyncMessengerChannelControlBlock) ccb;
            Channel channel = cb.getChannel().getNativeHandle();
            channel.attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASYNC_MSG_HANDLE_KEY ) ).set( handler );
            channel.attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_ASY_EXCLUSIVE_HANDLE_KEY ) ).set( true );
            channel.attr( AttributeKey.valueOf( WolfMCStandardConstants.CB_EXTERNAL_CHANNEL_KEY ) ).set( true );
            cb.sendAsynMsg( instructMessage, true );

            this.getLogger().info( "Embracing and registering passive controlled channel ({}).", cb.getChannel().getNativeHandle().id() );
        }
    }

    @Override
    public void embraces( int nLine, UMCTExpressHandler handler ) throws IOException {
        this.embraces( nLine, UlfAsyncMsgHandleAdapter.wrap( handler ) );
    }

    @Override
    public void embraces( int nLine ) throws IOException {
        this.embraces( nLine, this.mRouteDispatcher.getUMCTExpress() );
    }

    @Override
    public void createPassiveChannel( int nLine ) {
        ChannelPool pool = this.getMessageNode().getChannelPool();

        ChannelControlBlock[] cbs = new ChannelControlBlock[ nLine ];
        for ( int i = 0; i < nLine; ++i ) {
            ChannelControlBlock ccb = pool.depriveIdleChannel();
            if ( ccb == null ) {
                for ( int j = 0; j < nLine; ++j ) {
                    if ( cbs[ j ] == null ) {
                        break;
                    }
                    ChannelId id = (ChannelId)cbs[ j ].getChannel().getChannelID();
                    this.mInstructedChannels.remove( id );
                    pool.add( cbs[ j ] );
                }
                throw new IllegalArgumentException( "Creating `PassiveChannel` is compromised due to insufficient free channels. Consider setting up sufficient parallel channels." );
            }

            ChannelId id = (ChannelId)ccb.getChannel().getChannelID();
            cbs[ i ] = ccb;
            this.mInstructedChannels.put( id, ccb );
        }
    }
}
