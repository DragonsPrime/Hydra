package com.pinecone.hydra.umct.appoint;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.DynamicMessage;
import com.pinecone.hydra.umc.msg.ChannelPool;
import com.pinecone.hydra.umc.msg.RecipientChannelControlBlock;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.event.ChannelEventHandler;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfChannelStatus;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.server.UlfServer;
import com.pinecone.hydra.umc.wolfmc.server.WolfMCServer;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.appoint.proxy.GenericPassiveClientIfaceProxyFactory;
import com.pinecone.hydra.umct.appoint.proxy.PassiveClientIfaceProxyFactory;
import com.pinecone.hydra.umct.husky.HuskyCTPConstants;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodPrototype;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcher;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;
import com.pinecone.hydra.umct.mapping.ControllerInspector;

public class WolvesAppointServer extends WolfAppointServer implements DuplexAppointServer {
    protected static Class<?> checkExpressType( Class<?> expressType ) {
        if ( !DuplexExpress.class.isAssignableFrom( expressType ) ) {
            throw new IllegalArgumentException( "`" + expressType.getSimpleName() + "` is not DuplexExpress calibre qualified." );
        }
        return expressType;
    }

    protected PassiveClientIfaceProxyFactory mPassiveClientIfaceProxyFactory;

    protected void initUlfServerEventHandlers( UlfServer messenger ) {
        messenger.addDataArrivedEventHandlers(new ChannelEventHandler() {
            @Override
            public void afterEventTriggered( RecipientChannelControlBlock block ) {
                if ( block.getChannel().getChannelStatus() == UlfChannelStatus.WAITING_PASSIVE_RECEIVE ) {
                    ChannelPool pool = WolvesAppointServer.this.getUMCTExpress().getPoolByClientId( block.getChannel().getIdentityID() );
                    if ( pool != null ) {
                        pool.setIdleChannel( block );
                    }
                }
            }
        });
    }

    protected void initSelf( UlfServer messenger ) {
        this.initUlfServerEventHandlers( messenger );
        this.mPassiveClientIfaceProxyFactory = new GenericPassiveClientIfaceProxyFactory( this );
    }

    protected WolvesAppointServer( UlfServer messenger, RouteDispatcher dispatcher ){
        super( messenger, dispatcher );
        this.initSelf( messenger );
    }

    public WolvesAppointServer( UlfServer messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( compiler, controllerInspector, express ) );
    }

    public WolvesAppointServer( UlfServer messenger, CompilerEncoder encoder, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( encoder, express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolvesAppointServer( UlfServer messenger, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolvesAppointServer( UlfServer messenger, Class<?> expressType ){
        super( messenger, WolvesAppointServer.checkExpressType( expressType ) );
        this.initSelf( messenger );
    }

    public WolvesAppointServer( UlfServer messenger ){
        this( messenger, HuskyDuplexExpress.class );
    }


    @Override
    public boolean supportDuplex() {
        return true;
    }


    @Override
    public DuplexExpress getUMCTExpress() {
        return (DuplexExpress) super.getUMCTExpress();
    }

    @Override
    public void sendAsynMsg( long clientId, UMCMessage request, boolean bNoneBuffered, AsynMsgHandler handler ) throws IOException {
        this.getUMCTExpress().sendAsynMsg( clientId, request, bNoneBuffered, handler );
    }

    @Override
    public void sendAsynMsg( long clientId, UMCMessage request, boolean bNoneBuffered, UlfAsyncMsgHandleAdapter handler ) throws IOException {
        this.getUMCTExpress().sendAsynMsg( clientId, request, bNoneBuffered, handler );
    }

    @Override
    public void sendAsynMsg( long clientId, UMCMessage request, AsynMsgHandler handler ) throws IOException {
        this.getUMCTExpress().sendAsynMsg( clientId, request, true, handler );
    }



    @Override
    public void invokeInformAsyn( long clientId, MethodPrototype method, Object[] args, AsynMsgHandler handler ) throws IOException {
        DynamicMessage message = this.reinterpretMsg( method, args );
        this.sendAsynMsg( clientId, new UlfInformMessage(message.toByteArray()), handler );
    }

    @Override
    public void invokeInformAsyn( long clientId, MethodPrototype method, Object[] args, AsynReturnHandler handler ) throws IOException {
        DynamicMessage message = this.reinterpretMsg( method, args );
        this.sendAsynMsg(clientId, new UlfInformMessage( message.toByteArray(), HuskyCTPConstants.HCTP_DUP_CONTROL_PASSIVE_REQUEST ), new AsynMsgHandler() {
            @Override
            public void onSuccessfulMsgReceived( UMCMessage msg ) throws Exception {
                handler.onSuccessfulReturn( WolvesAppointServer.this.unmarshalResponse( method, msg ) );
            }

            @Override
            public void onErrorMsgReceived( UMCMessage msg ) throws Exception {
                handler.onErrorMsgReceived( msg );
            }
        });
    }

    @Override
    public void invokeInformAsyn( long clientId, String szMethodAddress, Object[] args, AsynMsgHandler handler ) throws IOException {
        this.invokeInformAsyn( clientId, this.queryMethodPrototype( szMethodAddress ), args, handler );
    }

    @Override
    public void invokeInformAsyn( long clientId, String szMethodAddress, Object[] args, AsynReturnHandler handler ) throws IOException {
        this.invokeInformAsyn( clientId, this.queryMethodPrototype( szMethodAddress ), args, handler );
    }



    @Override
    public Object invokeInform( long clientId, MethodPrototype method, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException {
        CompletableFuture<Object> future = new CompletableFuture<>();
        DynamicMessage message = this.reinterpretMsg(method, args);

        this.sendAsynMsg(clientId, new UlfInformMessage( message.toByteArray(), HuskyCTPConstants.HCTP_DUP_CONTROL_PASSIVE_REQUEST ), new AsynMsgHandler() {
            @Override
            public void onSuccessfulMsgReceived( UMCMessage msg ) throws Exception {
                try {
                    Object result = WolvesAppointServer.this.unmarshalResponse( method, msg );
                    future.complete(result);
                }
                catch ( IlleagalResponseException e ) {
                    future.completeExceptionally( e );
                }
            }

            @Override
            public void onErrorMsgReceived( UMCMessage msg ) throws Exception {
                future.completeExceptionally( new IlleagalResponseException( "Error message received: " + msg ) );
            }

            @Override
            public void onError( Object data, Throwable cause ) {
                future.completeExceptionally( cause );
            }
        });

        try {
            if ( nWaitTimeMil == -1 ) {
                if ( this.getMessageNode() instanceof WolfMCServer) {
                    nWaitTimeMil = ((WolfMCServer) this.getMessageNode()).getConnectionArguments().getKeepAliveTimeout() * 1000L;
                }
            }

            return WolfAppointHelper.evalCompletableFuture( future, nWaitTimeMil );
        }
        catch ( TimeoutException | ExecutionException e ) {
            throw new IlleagalResponseException( e );
        }
        catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new IlleagalResponseException( e );
        }
    }

    @Override
    public Object invokeInform( long clientId, MethodPrototype method, Object... args ) throws IlleagalResponseException, IOException {
        return this.invokeInform( clientId, method, args, -1 );
    }

    @Override
    public Object invokeInform( long clientId, String szMethodAddress, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException {
        return this.invokeInform( clientId, this.queryMethodPrototype( szMethodAddress ), args, nWaitTimeMil );
    }

    @Override
    public Object invokeInform( long clientId, String szMethodAddress, Object... args ) throws IlleagalResponseException, IOException {
        return this.invokeInform( clientId, this.queryMethodPrototype( szMethodAddress ), args );
    }


    @Override
    public <T> T getIface( long clientId, Class<T> iface ) {
        return this.mPassiveClientIfaceProxyFactory.createProxy( clientId, iface );
    }
}
