package com.pinecone.hydra.umq;

import com.pinecone.framework.system.prototype.Pinenut;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

public class RocketMQConsumer implements Pinenut {
    protected String pNameSrvAddr;
    protected String groupName;
    protected String topic;
    protected String tag;
    protected DefaultMQPushConsumer mqConsumer;

    public RocketMQConsumer(String pNameSrvAddr, String groupName, String topic, String tag) {
        this.pNameSrvAddr = pNameSrvAddr; // 消费者的nameService地址
        this.groupName = groupName;       // 消费者组名
        this.topic = topic;               // 订阅的主题
        this.tag = tag;                   // 订阅的标签
        this.mqConsumer = this.getDefaultMQConsumer();
    }

    private DefaultMQPushConsumer getDefaultMQConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.groupName);
        consumer.setNamesrvAddr(this.pNameSrvAddr);
        try {
            consumer.subscribe(this.topic, this.tag);
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    try {
                        for (MessageExt msg : msgs) {
                            String messageBody = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
                            System.out.println("Received message: " + messageBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER; // 返回稍后再消费
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; // 消费成功
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            System.out.println("Failed to start the consumer: " + e.getErrorMessage());
        }
        return consumer;
    }

    /**
     * 关闭消费者
     */
    public void shutdown() {
        if (mqConsumer != null) {
            mqConsumer.shutdown();
        }
    }

}
