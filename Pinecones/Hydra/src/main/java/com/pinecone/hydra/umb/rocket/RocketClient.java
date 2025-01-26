package com.pinecone.hydra.umb.rocket;

import java.util.function.Supplier;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.pinecone.hydra.umb.broadcast.BroadcastNode;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;

public interface RocketClient extends BroadcastNode {

    RocketConfig getRocketConfig();

    BroadcastProducer createProducer(Supplier<DefaultMQProducer> producerSupplier );

}
