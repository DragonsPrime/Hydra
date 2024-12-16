package com.pinecone.hydra.umct.appoint;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.wolfmc.server.UlfServer;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.MessageHandler;
import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.ProtoletMsgDeliver;
import com.pinecone.hydra.umct.SergeantExpress;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.husky.machinery.HuskyMarshal;
import com.pinecone.hydra.umct.mapping.BytecodeControllerInspector;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.hydra.umct.mapping.InspectException;
import com.pinecone.hydra.umct.mapping.MappingDigest;
import com.pinecone.hydra.umct.husky.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.husky.compiler.IfaceMappingDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.stereotype.IfaceUtils;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

import javassist.ClassPool;
import javassist.NotFoundException;

public class WolfAppointServer extends ArchAppointNode implements DuplexAppointServer {
    protected UlfServer                     mRecipient;
    protected UMCTExpress                   mUMCTExpress;
    protected MessageDeliver                mDefaultDeliver;

    protected void applyExpress( InterfacialCompiler compiler, UMCTExpress express ) {
        this.mUMCTExpress = express;
        this.mRecipient.apply( express );

        this.mDefaultDeliver      = new ProtoletMsgDeliver( AppointServer.DefaultEntityName, this.mUMCTExpress, compiler.getCompilerEncoder() );
        this.mUMCTExpress.register( this.mDefaultDeliver  );
    }

    protected WolfAppointServer( UlfServer messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector ){
        super( (Servgramium) messenger, new HuskyMarshal( compiler, controllerInspector, new GenericFieldProtobufDecoder() ) );
        this.mRecipient   = messenger;
    }

    public WolfAppointServer( UlfServer messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector, UMCTExpress express ){
        this( messenger, compiler, controllerInspector );
        this.applyExpress( compiler, express );
    }

    public WolfAppointServer( UlfServer messenger, CompilerEncoder encoder, UMCTExpress express ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader(), encoder
        ), new BytecodeControllerInspector(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ), express );
    }

    public WolfAppointServer( UlfServer messenger, UMCTExpress express ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ), new BytecodeControllerInspector(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ), express );
    }

    public WolfAppointServer( UlfServer messenger, Class<?> expressType ){
        this(
                messenger,
                new BytecodeIfacCompiler( ClassPool.getDefault(), messenger.getTaskManager().getClassLoader() ),
                new BytecodeControllerInspector( ClassPool.getDefault(), messenger.getTaskManager().getClassLoader() )
        );

        try{
            Constructor<?> constructor = expressType.getConstructor( String.class, MessageJunction.class );
            UMCTExpress express = (UMCTExpress) constructor.newInstance(AppointServer.DefaultEntityName, this);

            this.applyExpress(
                    this.mPMCTMarshal.getInterfacialCompiler(), express
            );
        }
        catch ( NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            throw new IllegalArgumentException( "`" + expressType.getSimpleName() + "` is not UMCTExpress calibre qualified." );
        }
    }

    public WolfAppointServer( UlfServer messenger ){
        this( messenger, WolfMCExpress.class );
    }

    @Override
    public UlfServer getMessageNode() {
        return this.mRecipient;
    }

    @Override
    public WolfAppointServer apply( UMCTExpress handler ) {
        this.mUMCTExpress = handler;
        this.mRecipient.apply( handler );
        return this;
    }

    @Override
    public UMCTExpress getUMCTExpress() {
        return this.mUMCTExpress;
    }

    @Override
    public MessageExpress register( Deliver deliver ) {
        return this.mUMCTExpress.register( deliver );
    }

    @Override
    public MessageExpress  fired   ( Deliver deliver ) {
        return this.mUMCTExpress.fired( deliver );
    }

    @Override
    public MessageDeliver getDeliver( String name ) {
        return this.mUMCTExpress.getDeliver( name );
    }

    @Override
    public MessageDeliver getDefaultDeliver() {
        return this.mDefaultDeliver;
    }

    @Override
    public boolean supportDuplex() {
        return this.mUMCTExpress instanceof SergeantExpress;
    }

    protected void registerInstance( MessageDeliver deliver, Object instance, Class<?> iface ) {
        if ( !iface.isInterface() ) {
            throw new IllegalArgumentException( "The provided class is not an interface: " + iface.getName() );
        }

        List<MethodDigest> digests = this.compile( iface, false ).getMethodDigests();
        Map<String, MethodDigest > digestMap = digests.stream()
                .collect( Collectors.toMap(MethodDigest::getName, digest -> digest) );

        Method[] methods = iface.getMethods();
        for ( Method method : methods ) {
            String methodName = IfaceUtils.getIfaceMethodName( method );

            DynamicMethodPrototype digest = (DynamicMethodPrototype)digestMap.get( methodName );

            String fullPath = digest.getFullName();

            MessageHandler handler = new MessageHandler() {
                @Override
                public String getAddressMapping() {
                    return digest.getFullName();
                }

                @Override
                public Object invoke( Object... args ) throws Exception {
                    return method.invoke( instance, args );
                }

                @Override
                public List<String> getArgumentsKey() {
                    return digest.getArgumentsKey();
                }

                @Override
                public Object getReturnDescriptor() {
                    return digest.getReturnDescriptor();
                }

                @Override
                public Object getArgumentsDescriptor() {
                    return digest.getArgumentsDescriptor();
                }

            };

            deliver.registerHandler( fullPath, handler );
            this.mPMCTMarshal.getMessageHandlerMap().put( fullPath, handler );
        }
    }

    @Override
    public void registerInstance( String deliverName, Object instance, Class<?> iface ) {
        MessageDeliver deliver = this.getDeliver( deliverName );
        if ( deliver == null ) {
            throw new IllegalArgumentException( "No such deliver: " + deliverName );
        }

        this.registerInstance( deliver, instance, iface );
    }

    @Override
    public void registerInstance( Object instance, Class<?> iface ) {
        this.registerInstance( this.mDefaultDeliver, instance, iface );
    }

    protected void registerController( MessageDeliver deliver, Object instance, Class<?> controllerType ) {
        try{
            List<MappingDigest> digests   = this.mPMCTMarshal.getControllerInspector().characterize( controllerType );
            List<IfaceMappingDigest>  ifs = this.getInterfacialCompiler().compile( digests );

            for ( IfaceMappingDigest imd : ifs ) {
                String[] addresses = imd.getAddresses();
                for ( int i = 0; i < addresses.length; ++i ) {
                    String address = addresses[ i ];

                    MessageHandler handler = new MessageHandler() {
                        @Override
                        public String getAddressMapping() {
                            return address;
                        }

                        @Override
                        public Object invoke( Object... args ) throws Exception {
                            return imd.getMappedMethod().invoke( instance, args );
                        }

                        @Override
                        public List<String> getArgumentsKey() {
                            return imd.getArgumentsKey();
                        }

                        @Override
                        public Object getReturnDescriptor() {
                            return imd.getReturnDescriptor();
                        }

                        @Override
                        public Object getArgumentsDescriptor() {
                            return imd.getArgumentsDescriptor();
                        }

                    };

                    deliver.registerHandler( address, handler );
                    this.mPMCTMarshal.getMessageHandlerMap().put( address, handler );
                }
            }
        }
        catch ( NotFoundException e ) {
            throw new InspectException( e );
        }
    }

    @Override
    public void registerController( String deliverName, Object instance, Class<?> controllerType ) {
        MessageDeliver deliver = this.getDeliver( deliverName );
        if ( deliver == null ) {
            throw new IllegalArgumentException( "No such deliver: " + deliverName );
        }

        this.registerController( deliver, instance, controllerType );
    }

    @Override
    public void registerController( Object instance, Class<?> controllerType ) {
        this.registerController( this.mDefaultDeliver, instance, controllerType );
    }
}
