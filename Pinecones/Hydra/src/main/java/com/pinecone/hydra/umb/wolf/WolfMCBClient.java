package com.pinecone.hydra.umb.wolf;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umb.broadcast.BroadcastControlProducer;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastNode;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

/**
 *  Pinecone Ursus For Java Wolf-UMCT-B [ Uniform Message Broadcast Control Transmit ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Transmit Protocol - Broadcast [UMC-T-B]
 *  统一消息广播传输控制传输协议
 *  **********************************************************
 */
public class WolfMCBClient extends ArchServgramium implements BroadcastControlNode {

    protected PMCTContextMachinery          mPMCTContextMachinery;

    protected RouteDispatcher               mRouteDispatcher;

    protected UMCBroadcastNode              mUMCBroadcastNode;


    public WolfMCBClient( UMCBroadcastNode broadcastNode, RouteDispatcher routeDispatcher, PMCTContextMachinery machinery, String szGramName, Processum parent ) {
        super( szGramName, parent );

        this.mPMCTContextMachinery = machinery;
        this.mRouteDispatcher      = routeDispatcher;
        this.mUMCBroadcastNode     = broadcastNode;
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
