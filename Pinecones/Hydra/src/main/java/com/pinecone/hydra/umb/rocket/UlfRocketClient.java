package com.pinecone.hydra.umb.rocket;

import java.util.function.Supplier;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.pinecone.hydra.umb.broadcast.UMCBroadcastNode;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastProducer;

public interface UlfRocketClient extends RocketClient, UMCBroadcastNode {

    UMCBroadcastProducer createUlfProducer( Supplier<DefaultMQProducer> producerSupplier ) ;

}
