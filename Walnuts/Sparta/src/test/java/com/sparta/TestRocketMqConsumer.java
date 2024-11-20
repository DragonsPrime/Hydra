package com.sparta;

import com.pinecone.hydra.umq.RocketMQConsumer;

public class TestRocketMqConsumer {
    public static void main(String[] args) {
        String nameSrvAddr = "127.0.0.1:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tag = "*"; // 订阅所有标签

        RocketMQConsumer rocketMQConsumer = new RocketMQConsumer(nameSrvAddr, groupName, topic, tag);

        // 消费者会持续运行，监听并消费消息
        System.out.println("Consumer started, waiting for messages...");

        // 可以在适当的时候关闭消费者
        // rocketMQConsumer.shutdown();
    }
}
