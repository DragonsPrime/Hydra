package com.pinecone.hydra.umb.rocket;

import java.util.function.Supplier;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

import com.pinecone.hydra.umb.broadcast.BroadcastNode;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.handler.ErrorMessageAudit;

public interface RocketClient extends BroadcastNode {

    @Override
    default ErrorMessageAudit    getErrorMessageAudit() {
        return null;
    }

    @Override
    default void                 setErrorMessageAudit( ErrorMessageAudit audit ){

    }



    RocketConfig getRocketConfig();

    BroadcastProducer createProducer(Supplier<DefaultMQProducer> producerSupplier );

}
