package com.sparta;

import com.pinecone.hydra.umq.RocketMQClient;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;

public class TestRocketClient {
    public static void main(String[] args) {
        String nameSrvAddr = "127.0.0.1:9876";
        String groupName = "testGroup";

        RocketMQClient rocketMQClient = new RocketMQClient(nameSrvAddr, groupName);

        String topic = "testTopic";
        String tags = "testTags";
        String keys = "testKeys";
        String body = "This is a test message";

        try {
            boolean result = rocketMQClient.sendMessage(topic, tags, keys, body.getBytes(RemotingHelper.DEFAULT_CHARSET));
            if (result) {
                System.out.println("Message sent successfully");
            } else {
                System.out.println("Failed to send message");
            }
        } catch (MQClientException e) {
            System.out.println("Failed to send message: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // 关闭生产者
        rocketMQClient.shutdown();
    }
}
