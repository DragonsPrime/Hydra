package com.pinecone.hydra.umct.appoint;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.msg.ChannelControlBlock;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umc.wolfmc.client.UlfClient;
import com.pinecone.hydra.umc.wolfmc.client.WolfMCClient;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.protocol.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.protocol.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.protocol.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.protocol.compiler.InterfacialCompiler;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

import io.netty.channel.ChannelHandlerContext;
import javassist.ClassPool;

public class WolfAppointClient extends ArchAppointNode implements AppointClient {
    protected UlfClient mMessenger;

    public WolfAppointClient( UlfClient messenger, InterfacialCompiler compiler ){
        super( (Servgramium) messenger ,compiler, new GenericFieldProtobufDecoder() );
        this.mMessenger = messenger;
    }

    public WolfAppointClient( UlfClient messenger, CompilerEncoder encoder ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader(), encoder
        ) );
    }

    public WolfAppointClient( UlfClient messenger ){
        this( messenger, new BytecodeIfacCompiler(
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
        this.mMessenger.sendAsynMsg(request, new UlfAsyncMsgHandleAdapter() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                handler.onSuccessfulMsgReceived( block.getTransmit(), block.getReceiver(), msg );
            }

            @Override
            public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                handler.onSuccessfulMsgReceived( transmit, receiver, msg );
            }

            @Override
            public void onErrorMsgReceived( Medium medium, ChannelControlBlock block, UMCMessage msg, ChannelHandlerContext ctx, Object rawMsg ) throws Exception {
                handler.onErrorMsgReceived( block.getTransmit(), block.getReceiver(), msg );
            }

            @Override
            public void onErrorMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                handler.onErrorMsgReceived( transmit, receiver, msg );
            }

            @Override
            public void onError( Object data, Throwable cause ) {
                handler.onError( data, cause );
            }
        });
    }

    protected CompilerEncoder getCompilerEncoder() {
        return this.getInterfacialCompiler().getCompilerEncoder();
    }

    protected DynamicMessage reinterpretMsg( DynamicMethodPrototype prototype, Object[] args ) {
        FieldProtobufEncoder encoder = this.getFieldProtobufEncoder();
        Descriptors.Descriptor descriptor = prototype.getArgumentsDescriptor();

        FieldEntity[] types = prototype.getArgumentTemplate().getSegments();
        for ( int i = 0; i < args.length; ++i ) {
            types[ i + 1 ].setValue( args [ i ] ); // 1 for path argument.
        }

        return encoder.encode(
                descriptor, types, this.getCompilerEncoder().getExceptedKeys(), this.getCompilerEncoder().getOptions()
        );
    }


    public Object unmarshalResponse( DynamicMethodPrototype digest, byte[] raw ) throws IlleagalResponseException {
        try{
            Descriptors.Descriptor retDes = digest.getReturnDescriptor();
            DynamicMessage rm = DynamicMessage.parseFrom( retDes, raw );
            GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
            return decoder.decode(
                    digest.getReturnType(), retDes, rm, this.getCompilerEncoder().getExceptedKeys(), this.getCompilerEncoder().getOptions()
            );
        }
        catch ( InvalidProtocolBufferException e ) {
            throw new IlleagalResponseException( e );
        }
    }

    public Object unmarshalResponse( DynamicMethodPrototype digest, UMCMessage msg ) throws IlleagalResponseException {
        return this.unmarshalResponse( digest, (byte[]) msg.getHead().getExtraHead() );
    }

    @Override
    public void invokeInformAsyn( DynamicMethodPrototype method, Object[] args, AsynMsgHandler handler ) throws IOException {
        DynamicMessage message = this.reinterpretMsg( method, args );
        this.sendAsynMsg( new UlfInformMessage(message.toByteArray()), handler );
    }

    @Override
    public void invokeInformAsyn( DynamicMethodPrototype method, Object[] args, AsynReturnHandler handler ) throws IOException {
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
    public Object invokeInform( DynamicMethodPrototype method, Object[] args, long nWaitTimeMil ) throws IlleagalResponseException, IOException {
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
            Object ret;
            if ( nWaitTimeMil == -1 ) {
                if ( this.mMessenger instanceof WolfMCClient ) {
                    nWaitTimeMil = ((WolfMCClient) this.mMessenger).getConnectionArguments().getKeepAliveTimeout() * 1000;
                }
            }

            if ( nWaitTimeMil != -1 ) {
                ret = future.get( nWaitTimeMil, TimeUnit.MILLISECONDS );
            }
            else {
                ret = future.get();
            }

            if ( ret instanceof Exception ) {
                throw new IlleagalResponseException( (Exception)ret );
            }

            return ret;
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
    public Object invokeInform( DynamicMethodPrototype method, Object... args ) throws IlleagalResponseException, IOException {
        return this.invokeInform( method, args, -1 );
    }

    protected DynamicMethodPrototype queryMethodPrototype( String szMethodAddress ) {
        DynamicMethodPrototype method = (DynamicMethodPrototype) this.queryMethodDigest( szMethodAddress );
        if ( method == null ) {
            throw new IllegalArgumentException( "Method address `" + szMethodAddress + "` is invalid." );
        }

        return method;
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

}
