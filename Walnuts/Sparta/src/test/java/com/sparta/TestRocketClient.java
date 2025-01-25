package com.sparta;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umb.UlfPackageMessageHandler;
import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.rocket.RocketClient;
import com.pinecone.hydra.umb.rocket.UlfBroadcastProducer;
import com.pinecone.hydra.umb.rocket.RocketMQClient;

import org.apache.rocketmq.client.exception.MQClientException;

public class TestRocketClient {
    public static void main(String[] args) throws Exception {
        String nameSrvAddr = "localhost:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tags = "*";
        String keys = "testKeys";
        String body = "This is a test message";


        RocketClient client = new RocketMQClient( nameSrvAddr, groupName );
        BroadcastConsumer consumer = client.createConsumer( topic );
        consumer.start(new UlfPackageMessageHandler() {
            @Override
            public void onSuccessfulMsgReceived( byte[] body, Object[] args ) throws Exception {
                Debug.trace( new String( body ) );
            }
        });



        BroadcastProducer producer = client.createProducer();
        producer.start();

        Debug.sleep( 100000 );

    }
}
