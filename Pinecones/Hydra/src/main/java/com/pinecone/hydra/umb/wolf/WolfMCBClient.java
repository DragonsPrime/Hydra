package com.pinecone.hydra.umb.wolf;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umb.broadcast.BroadcastControlProducer;
import com.pinecone.hydra.umb.broadcast.BroadcastNode;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastNode;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.handler.ErrorMessageAudit;
import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.husky.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.husky.machinery.HuskyContextMachinery;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcher;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcherFabricator;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;
import com.pinecone.hydra.umct.mapping.BytecodeControllerInspector;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

import javassist.ClassPool;

/**
 *  Pinecone Ursus For Java Wolf-UMCT-B [ Uniform Message Broadcast Control Transmit ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Transmit Protocol - Broadcast [UMC-T-B]
 *  统一消息广播传输控制传输协议
 *  **********************************************************
 */
public class WolfMCBClient extends ArchServgramium implements UlfBroadcastControlNode {

    protected PMCTContextMachinery          mPMCTContextMachinery;

    protected RouteDispatcher               mRouteDispatcher;

    protected UMCBroadcastNode              mUMCBroadcastNode;


    public WolfMCBClient( UMCBroadcastNode broadcastNode, @Nullable RouteDispatcher routeDispatcher, PMCTContextMachinery machinery, String szGramName, Processum parent ) {
        super( szGramName, parent );

        this.mPMCTContextMachinery = machinery;
        this.mRouteDispatcher      = routeDispatcher;
        this.mUMCBroadcastNode     = broadcastNode;
    }

    public WolfMCBClient( UMCBroadcastNode broadcastNode, PMCTContextMachinery machinery, String szGramName, Processum parent ) {
        this( broadcastNode, null, machinery, szGramName, parent );

        this.mRouteDispatcher = this.createHuskyRoute();
    }

    public WolfMCBClient( UMCBroadcastNode broadcastNode, PMCTContextMachinery machinery, String szGramName, Processum parent, Class<?> expressType ) {
        this( broadcastNode, null, machinery, szGramName, parent );

        UMCTExpress express   = this.createUMCTExpress( BroadcastNode.DefaultEntityName, expressType );
        this.mRouteDispatcher = this.createHuskyRoute( express );
        HuskyRouteDispatcherFabricator.afterConstructed( (HuskyRouteDispatcher)this.mRouteDispatcher, express );
    }

    public WolfMCBClient( UMCBroadcastNode broadcastNode, String szGramName, Processum parent, Class<?> expressType ) {
        this(
                broadcastNode,
                null,

                new HuskyContextMachinery( new BytecodeIfacCompiler(
                        ClassPool.getDefault(), parent.getTaskManager().getClassLoader()
                ), new BytecodeControllerInspector(
                        ClassPool.getDefault(),  parent.getTaskManager().getClassLoader()
                ), new GenericFieldProtobufDecoder() ),

                szGramName,
                parent
        );

        UMCTExpress express   = this.createUMCTExpress( BroadcastNode.DefaultEntityName, expressType );
        this.mRouteDispatcher = this.createHuskyRoute( express );
        HuskyRouteDispatcherFabricator.afterConstructed( (HuskyRouteDispatcher)this.mRouteDispatcher, express );
    }


    @Override
    public long getMessageNodeId() {
        return this.mUMCBroadcastNode.getMessageNodeId();
    }

    @Override
    public ErrorMessageAudit getErrorMessageAudit() {
        return this.mUMCBroadcastNode.getErrorMessageAudit();
    }

    @Override
    public void setErrorMessageAudit( ErrorMessageAudit audit ) {
        this.mUMCBroadcastNode.setErrorMessageAudit( audit );
    }




    @Override
    public void applyPMCTContextMachinery( PMCTContextMachinery mPMCTContextMachinery ) {
        this.mPMCTContextMachinery = mPMCTContextMachinery;
    }

    @Override
    public void applyRouteDispatcher( RouteDispatcher mRouteDispatcher ) {
        this.mRouteDispatcher = mRouteDispatcher;
    }

    @Override
    public UMCTExpress createUMCTExpress( String name, Class<?> expressType ) {
        try{
            Constructor<?> constructor = expressType.getConstructor( String.class, MessageJunction.class, Logger.class );
            return  (UMCTExpress) constructor.newInstance( name, this, this.getLogger() );
        }
        catch ( NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            throw new IllegalArgumentException( "`" + expressType.getSimpleName() + "` is not UMCTExpress calibre qualified." );
        }
    }

    @Override
    public UMCTExpress createUlfExpress( String name ) {
        return this.createUMCTExpress( name, WolfMCExpress.class );
    }

    @Override
    public RouteDispatcher createHuskyRoute() {
        return new HuskyRouteDispatcher( this.getTaskManager().getClassLoader(), true );
    }

    @Override
    public RouteDispatcher createHuskyRoute( UMCTExpress express ) {
        RouteDispatcher dispatcher = this.createHuskyRoute();
        dispatcher.setUMCTExpress( express );
        return dispatcher;
    }



    @Override
    public InterfacialCompiler getInterfacialCompiler() {
        return this.mPMCTContextMachinery.getInterfacialCompiler();
    }

    @Override
    public PMCTContextMachinery getPMCTTransformer() {
        return this.mPMCTContextMachinery;
    }

    @Override
    public RouteDispatcher getRouteDispatcher() {
        return this.mRouteDispatcher;
    }

    @Override
    public FieldProtobufEncoder getFieldProtobufEncoder() {
        return this.mPMCTContextMachinery.getFieldProtobufEncoder();
    }

    @Override
    public FieldProtobufDecoder getFieldProtobufDecoder() {
        return this.mPMCTContextMachinery.getFieldProtobufDecoder();
    }




    @Override
    public ClassDigest queryClassDigest( String name ) {
        return this.mPMCTContextMachinery.queryClassDigest( name );
    }

    @Override
    public MethodDigest queryMethodDigest( String name ) {
        return this.mPMCTContextMachinery.queryMethodDigest( name );
    }

    @Override
    public void addClassDigest( ClassDigest that ) {
        this.mPMCTContextMachinery.addClassDigest( that );
    }

    @Override
    public void addMethodDigest( MethodDigest that ) {
        this.mPMCTContextMachinery.addMethodDigest( that );
    }

    @Override
    public ClassDigest compile( Class<? > clazz, boolean bAsIface ) {
        return this.mPMCTContextMachinery.compile( clazz, bAsIface );
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






    @Override
    public BroadcastControlConsumer createBroadcastControlConsumer( UMCBroadcastConsumer workAgent, RouteDispatcher routeDispatcher ) {
        return new WolfMCBConsumer( this, routeDispatcher, workAgent );
    }

    @Override
    public BroadcastControlConsumer createBroadcastControlConsumer( UMCBroadcastConsumer workAgent ) {
        return this.createBroadcastControlConsumer( workAgent, this.getRouteDispatcher() );
    }

    @Override
    public BroadcastControlConsumer createBroadcastControlConsumer( UNT unt ) {
        return this.createBroadcastControlConsumer( this.createUlfConsumer( unt ), this.getRouteDispatcher() );
    }

    @Override
    public BroadcastControlConsumer createBroadcastControlConsumer( String topic, String ns ) {
        return this.createBroadcastControlConsumer( this.createUlfConsumer( topic, ns ), this.getRouteDispatcher() );
    }

    @Override
    public BroadcastControlConsumer createBroadcastControlConsumer( String topic ) {
        return this.createBroadcastControlConsumer( this.createUlfConsumer( topic ), this.getRouteDispatcher() );
    }



    @Override
    public BroadcastControlProducer createBroadcastControlProducer( UMCBroadcastProducer workAgent ) {
        return new WolfMCBProducer( this, workAgent );
    }

    @Override
    public BroadcastControlProducer createBroadcastControlProducer() {
        return this.createBroadcastControlProducer( this.createUlfProducer() );
    }








    @Override
    public UMCBroadcastNode getUMCBroadcastNode() {
        return this.mUMCBroadcastNode;
    }

    @Override
    public ExtraHeadCoder getExtraHeadCoder() {
        return this.mUMCBroadcastNode.getExtraHeadCoder();
    }

    @Override
    public UMCBroadcastProducer createUlfProducer() {
        return this.mUMCBroadcastNode.createUlfProducer();
    }

    @Override
    public UMCBroadcastConsumer createUlfConsumer( String topic, String ns ) {
        return this.mUMCBroadcastNode.createUlfConsumer( topic, ns );
    }

    @Override
    public UMCBroadcastConsumer createUlfConsumer( String topic ) {
        return this.mUMCBroadcastNode.createUlfConsumer( topic );
    }

    @Override
    public UMCBroadcastConsumer createUlfConsumer( UNT unt ) {
        return this.mUMCBroadcastNode.createUlfConsumer( unt );
    }

    @Override
    public void register( BroadcastProducer producer ) {
        this.mUMCBroadcastNode.register( producer );
    }

    @Override
    public void register( BroadcastConsumer consumer ) {
        this.mUMCBroadcastNode.register( consumer );
    }

    @Override
    public void deregister( BroadcastProducer producer ) {
        this.mUMCBroadcastNode.deregister( producer );
    }

    @Override
    public void deregister( BroadcastConsumer consumer ) {
        this.mUMCBroadcastNode.deregister( consumer );
    }

    @Override
    public BroadcastProducer createProducer() {
        return this.mUMCBroadcastNode.createUlfProducer();
    }

    @Override
    public BroadcastConsumer createConsumer( String topic, String ns ) {
        return this.mUMCBroadcastNode.createUlfConsumer( topic, ns );
    }

    @Override
    public BroadcastConsumer createConsumer( String topic ) {
        return this.mUMCBroadcastNode.createUlfConsumer( topic );
    }

    @Override
    public BroadcastConsumer createConsumer( UNT unt ) {
        return this.mUMCBroadcastNode.createUlfConsumer( unt );
    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public void close() {
        this.mUMCBroadcastNode.close();
    }

    @Override
    public void terminate() {
        this.close();
    }

}
