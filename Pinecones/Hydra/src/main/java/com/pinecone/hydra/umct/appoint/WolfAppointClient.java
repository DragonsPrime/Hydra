package com.pinecone.hydra.umct.appoint;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.DynamicMessage;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.client.UlfClient;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.appoint.proxy.GenericIfaceProxyFactory;
import com.pinecone.hydra.umct.appoint.proxy.IfaceProxyFactory;
import com.pinecone.hydra.umct.husky.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodPrototype;
import com.pinecone.hydra.umct.husky.machinery.HuskyContextMachinery;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;
import com.pinecone.hydra.umct.mapping.BytecodeControllerInspector;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

import javassist.ClassPool;

public class WolfAppointClient extends ArchAppointNode implements AppointClient {
    protected UlfClient              mMessenger;

    protected IfaceProxyFactory      mIfaceProxyFactory;

    protected WolfAppointClient( UlfClient messenger, boolean delay ){
        super( (Servgramium) messenger );
        this.mMessenger          = messenger;
        this.mIfaceProxyFactory  = new GenericIfaceProxyFactory( this );
    }

    public WolfAppointClient( UlfClient messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector ){
        this( messenger, true );
        this.mPMCTContextMachinery = new HuskyContextMachinery( compiler, controllerInspector, new GenericFieldProtobufDecoder() );
        this.mMessenger          = messenger;
        this.mIfaceProxyFactory  = new GenericIfaceProxyFactory( this );
    }

    public WolfAppointClient( UlfClient messenger, CompilerEncoder encoder ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader(), encoder
        ), new BytecodeControllerInspector(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ) );
    }

    public WolfAppointClient( UlfClient messenger ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ), new BytecodeControllerInspector(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ) );
    }



    @Override
    public UlfClient getMessageNode() {
        return this.mMessenger;
    }


    @Override
    public UMCMessage sendSyncMsg( UMCMessage request ) throws IOException {
        return this.sendSyncMsg( request, false );
    }

    @Override
    public UMCMessage sendSyncMsg( UMCMessage request, boolean bNoneBuffered ) throws IOException {
        return this.mMessenger.sendSyncMsg( request, bNoneBuffered );
    }

    @Override
    public void sendAsynMsg( UMCMessage request ) throws IOException {
        this.mMessenger.sendAsynMsg( request );
    }

    @Override
    public void sendAsynMsg( UMCMessage request, AsynMsgHandler handler ) throws IOException {
        this.mMessenger.sendAsynMsg( request, AsynMsgHandler.wrap( handler ) );
    }


    @Override
    public void invokeInformAsyn( MethodPrototype method, Object[] args, AsynMsgHandler handler ) throws IOException {
        DynamicMessage message = this.reinterpretMsg( method, args );
        this.sendAsynMsg( new UlfInformMessage(message.toByteArray()), handler );
    }

    @Override
    public void invokeInformAsyn( MethodPrototype method, Object[] args, AsynReturnHandler handler ) throws IOException {
        DynamicMessage message = this.reinterpretMsg( method, args );
        this.sendAsynMsg(new UlfInformMessage(message.toByteArray()), new AsynMsgHandler() {
            @Override
            public void onSuccessfulMsgReceived( UMCMessage msg ) throws Exception {
                handler.onSuccessfulReturn( WolfAppointClient.this.unmarshalResponse( method, msg ) );
            }

            @Override
            public void onErrorMsgReceived( UMCMessage msg ) throws Exception {
                handler.onErrorMsgReceived( msg );
            }
        });
    }

    @Override
    public Object invokeInform( MethodPrototype method, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException {
        CompletableFuture<Object> future = new CompletableFuture<>();
        DynamicMessage message = this.reinterpretMsg(method, args);

        this.sendAsynMsg(new UlfInformMessage(message.toByteArray()), new AsynMsgHandler() {
            @Override
            public void onSuccessfulMsgReceived( UMCMessage msg ) throws Exception {
                try {
                    Object result = WolfAppointClient.this.unmarshalResponse( method, msg );
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
                if ( this.mMessenger instanceof WolfMCClient ) {
                    nWaitTimeMil = ((WolfMCClient) this.mMessenger).getConnectionArguments().getKeepAliveTimeout() * 1000L;
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
    public Object invokeInform( MethodPrototype method, Object... args ) throws IlleagalResponseException, IOException {
        return this.invokeInform( method, args, -1 );
    }

    @Override
    public void invokeInformAsyn( String szMethodAddress, Object[] args, AsynMsgHandler handler ) throws IOException {
        this.invokeInformAsyn( this.queryMethodPrototype( szMethodAddress ), args, handler );
    }

    @Override
    public void invokeInformAsyn( String szMethodAddress, Object[] args, AsynReturnHandler handler ) throws IOException {
        this.invokeInformAsyn( this.queryMethodPrototype( szMethodAddress ), args, handler );
    }

    @Override
    public Object invokeInform( String szMethodAddress, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException {
        return this.invokeInform( this.queryMethodPrototype( szMethodAddress ), args, nWaitTimeMil );
    }

    @Override
    public Object invokeInform( String szMethodAddress, Object... args ) throws IlleagalResponseException, IOException {
        return this.invokeInform( this.queryMethodPrototype( szMethodAddress ), args );
    }

    @Override
    public <T> T getIface( Class<T> iface ) {
        return this.mIfaceProxyFactory.createProxy( iface );
    }

}
