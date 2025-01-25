package com.pinecone.hydra.umb.rocket;

import com.pinecone.hydra.umb.UMBClientException;
import com.pinecone.hydra.umb.broadcast.BroadcastNode;
import com.pinecone.hydra.umb.broadcast.UNT;
import com.pinecone.hydra.umc.msg.UMCMessage;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.function.Supplier;

public class WolfBroadcastProducer extends UlfBroadcastProducer {
    public WolfBroadcastProducer( RocketClient client, Supplier<DefaultMQProducer> producerSupplier ) {
        super( client, producerSupplier );
    }

    public WolfBroadcastProducer( RocketClient client ) {
        this( client, DefaultMQProducer::new );
    }

//    //@Override
//    public void sendMessage( String topic, String ns, String name, UMCMessage message ) throws UMBClientException {
//        this.sendMessage( topic, ns, name, message );
//    }
//
//    //@Override
//    public void sendMessage( String topic, UMCMessage message ) throws UMBClientException {
//        this.sendMessage( topic, "", BroadcastNode.DefaultEntityName, body );
//    }
//
//    //@Override
//    public void sendMessage(UNT unt, String name, UMCMessage message ) throws UMBClientException {
//        this.sendMessage( unt.getTopic(), unt.getNamespace(), name, body );
//    }


}
