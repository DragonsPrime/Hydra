package com.sparta;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umb.rocket.GenericMessageHandler;
import com.pinecone.hydra.umb.rocket.RocketMQCenter;
import com.pinecone.hydra.umb.rocket.RocketMQClient;

import org.apache.rocketmq.client.exception.MQClientException;

public class TestRocketClient {
    public static void main(String[] args) throws MQClientException {
        String nameSrvAddr = "localhost:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tags = "*";
        String keys = "testKeys";
        String body = "This is a test message";

        RocketMQCenter rocketMQCenter = new RocketMQCenter(nameSrvAddr, groupName, topic, tags);
        rocketMQCenter.listen(new GenericMessageHandler());



        RocketMQClient client = rocketMQCenter.getClient(nameSrvAddr, groupName);
        client.sendMessage( topic, tags, keys, "你好".getBytes() );
//        client.sendMessage( topic, tags, keys, "你好".getBytes() );
//        client.sendMessage( topic, tags, keys, "你好".getBytes() );
//        client.sendMessage( topic, tags, keys, "你好".getBytes() );
//        client.sendMessage( topic, tags, keys, "你好".getBytes() );
//        client.sendMessage( topic, tags, keys, "你好".getBytes() );





        //rocketMQCenter.stopListen();

        Debug.sleep( 100000 );

    }
}
