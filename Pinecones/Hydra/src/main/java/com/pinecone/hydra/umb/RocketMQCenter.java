package com.pinecone.hydra.umb;

import com.pinecone.framework.util.Debug;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class RocketMQCenter {
    protected String pNameSrvAddr;
    protected String groupName;
    protected String topic;

    protected String tag;

    protected DefaultMQPushConsumer mqConsumer;

    public RocketMQCenter( String pNameSrvAddr, String groupName, String topic, String tag ){
        this.pNameSrvAddr = pNameSrvAddr;
        this.groupName = groupName;
        this.topic = topic;
        this.tag = tag;
    }

    //这里在设置监听的时候要用户自己注册一个消费方式
    public void listen( MessageHandler messageHandler ){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(this.groupName);
        consumer.setNamesrvAddr(this.pNameSrvAddr);
        try {
            consumer.subscribe(this.topic, this.tag);
            consumer.setMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    for (MessageExt msg : msgs) {
                        String messageBody = new String(msg.getBody());
                        try {
                            boolean success = messageHandler.handleMessage(messageBody);
                            if (!success) {
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }
                        } catch (Exception e) {
                            System.err.println("Message processing failed: " + e.getMessage());
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (MQClientException e) {
            System.out.println("Failed to start the consumer: " + e.getErrorMessage());
        }
        this.mqConsumer = consumer;
        Debug.trace("开始监听");
    }

    public void stopListen(){
        if (this.mqConsumer != null) {
            this.mqConsumer.shutdown();
        }
    }

    public RocketMQClient getClient( String pNameSrvAddr, String groupName ) {
       return new RocketMQClient( pNameSrvAddr, groupName );
    }

}
