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
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.husky.machinery.HuskyContextMachinery;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcher;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcherFabricator;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;
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

public class WolfAppointServer extends ArchAppointNode implements AppointServer {
    protected UlfServer                     mRecipient;
    protected RouteDispatcher               mRouteDispatcher;

    protected void applyExpress( UMCTExpress express ) {
        this.mRecipient.apply( express );
    }

    protected WolfAppointServer( UlfServer messenger, RouteDispatcher dispatcher ){
        super( (Servgramium) messenger, dispatcher.getContextMachinery() );
        this.mRecipient       = messenger;
        this.mRouteDispatcher = dispatcher;
    }

    public WolfAppointServer( UlfServer messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( compiler, controllerInspector, express ) );
        this.apply( express );
    }

    public WolfAppointServer( UlfServer messenger, CompilerEncoder encoder, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( encoder, express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolfAppointServer( UlfServer messenger, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolfAppointServer( UlfServer messenger, Class<?> expressType ){
        this( messenger, new HuskyRouteDispatcher( messenger.getTaskManager().getClassLoader(), true ) );

        try{
            Constructor<?> constructor = expressType.getConstructor( String.class, MessageJunction.class );
            UMCTExpress express = (UMCTExpress) constructor.newInstance( AppointServer.DefaultEntityName, this );

            this.applyExpress( express );
            HuskyRouteDispatcherFabricator.afterConstructed( (HuskyRouteDispatcher)this.mRouteDispatcher, express );
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
        this.mRouteDispatcher.setUMCTExpress( handler );
        this.mRecipient.apply( handler );
        return this;
    }

    @Override
    public UMCTExpress getUMCTExpress() {
        return this.mRouteDispatcher.getUMCTExpress();
    }

    @Override
    public MessageExpress register( Deliver deliver ) {
        return this.mRouteDispatcher.register( deliver );
    }

    @Override
    public MessageExpress  fired   ( Deliver deliver ) {
        return this.mRouteDispatcher.fired( deliver );
    }

    @Override
    public MessageDeliver getDeliver( String name ) {
        return this.mRouteDispatcher.getDeliver( name );
    }

    @Override
    public MessageDeliver getDefaultDeliver() {
        return this.mRouteDispatcher.getDefaultDeliver();
    }

    @Override
    public void registerInstance( String deliverName, Object instance, Class<?> iface ) {
        this.mRouteDispatcher.registerInstance( deliverName, instance, iface );
    }

    @Override
    public void registerInstance( Object instance, Class<?> iface ) {
        this.mRouteDispatcher.registerInstance( instance, iface );
    }

    @Override
    public void registerController( String deliverName, Object instance, Class<?> controllerType ) {
        this.mRouteDispatcher.registerController( deliverName, instance, controllerType );
    }

    @Override
    public void registerController( Object instance, Class<?> controllerType ) {
        this.mRouteDispatcher.registerController( instance, controllerType );
    }
}
