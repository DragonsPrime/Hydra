package com.pinecone.hydra.umb.rocket;

import java.util.function.Supplier;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.pinecone.hydra.umb.broadcast.UMCBroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.extra.GenericExtraHeadCoder;

/**
 *  Pinecone Ursus For Java Wolf-UMC-RocketMQ [ Wolf, Uniform Message Control Protocol Client ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Protocol for RocketMQ Client
 *  统一消息广播控制客户端 (RocketMQ 版本)
 *  **********************************************************
 */
public class WolfMCRocketClient extends RocketMQClient implements UlfRocketClient {

    protected ExtraHeadCoder           mExtraHeadCoder;

    public WolfMCRocketClient( String nameSrvAddr, String groupName, ExtraHeadCoder extraHeadCoder ) {
        super( nameSrvAddr, groupName );

        this.mExtraHeadCoder           = extraHeadCoder;
    }

    public WolfMCRocketClient( String nameSrvAddr, String groupName ) {
        this( nameSrvAddr, groupName, new GenericExtraHeadCoder() );
    }

    @Override
    public ExtraHeadCoder getExtraHeadCoder() {
        return this.mExtraHeadCoder;
    }

    @Override
    public UMCBroadcastProducer createUlfProducer( Supplier<DefaultMQProducer> producerSupplier ) {
        UMCBroadcastProducer producer = new WolfBroadcastProducer( this, producerSupplier, this.mExtraHeadCoder );
        this.register( producer );
        return producer;
    }

    @Override
    public UMCBroadcastProducer createUlfProducer() {
        return this.createUlfProducer( DefaultMQProducer::new );
    }


    @Override
    public UMCBroadcastConsumer createUlfConsumer( String topic, String ns ) {
        UMCBroadcastConsumer consumer = new WolfPushConsumer( this, topic, ns, this.mExtraHeadCoder );
        this.register( consumer );
        return consumer;
    }

    @Override
    public UMCBroadcastConsumer createUlfConsumer( String topic ) {
        return this.createUlfConsumer( topic, "" );
    }

    @Override
    public UMCBroadcastConsumer createUlfConsumer( UNT unt ) {
        return this.createUlfConsumer( unt.getTopic(), unt.getNamespace() );
    }
}
