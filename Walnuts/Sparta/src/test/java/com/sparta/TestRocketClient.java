package com.sparta;

import com.pinecone.hydra.umq.GenericMessageHandler;
import com.pinecone.hydra.umq.RocketMQCenter;
import com.pinecone.hydra.umq.RocketMQClient;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TestRocketClient {
    public static void main(String[] args) throws MQClientException {
        String nameSrvAddr = "127.0.0.1:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tags = "*";
        String keys = "testKeys";
        String body = "This is a test message";

        RocketMQCenter rocketMQCenter = new RocketMQCenter(nameSrvAddr, groupName, topic, tags);
        rocketMQCenter.listen(new GenericMessageHandler());

        RocketMQClient client = rocketMQCenter.getClient(nameSrvAddr, groupName);

        client.sendMessage( topic, tags, keys, "你好".getBytes() );

        rocketMQCenter.stopListen();

    }
}
